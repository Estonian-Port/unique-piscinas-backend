package com.estonianport.unique.service

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.quartz.ProgramacionJobManager
import com.estonianport.unique.common.quartz.QuartzSchedulerService
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Programacion
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgramacionService(
    private val piscinaRepository: PiscinaRepository,
    private val piscinaService: PiscinaService,
    private val quartzSchedulerService: QuartzSchedulerService,
    private val programacionHelper: ProgramacionJobManager
) {
    @Transactional
    fun agregarProgramacion(
        piscinaId: Long,
        nuevaProgramacion: Programacion,
        estadoPiscinaService: EstadoPiscinaService
    ) {
        val piscina = piscinaService.findById(piscinaId)
        val patente = piscina.plaqueta.patente

        //Valido que no se solapen las programaciones
        validarSolapamientoProgramaciones(piscina, nuevaProgramacion)

        //Agrego la nueva programación a la coleccion de la piscina
        if (nuevaProgramacion.tipo == ProgramacionType.FILTRADO) {
            piscina.agregarProgramacionFiltrado(nuevaProgramacion)
        }
        if (nuevaProgramacion.tipo == ProgramacionType.ILUMINACION) {
            piscina.agregarProgramacionIluminacion(nuevaProgramacion)
        }

        //Guardo la piscina (con flush para obtener el ID de la nueva programación)
        piscinaRepository.saveAndFlush(piscina)

        val programacionGuardada = piscinaRepository.getLastProgramacion(piscinaId)
            ?: throw IllegalStateException("No se pudo obtener la programación recién guardada")

        val programacionId = programacionGuardada.id
            ?: throw IllegalStateException("La programación no tiene ID después del flush")

        try {
            programacionHelper.crearJobs(piscinaId, patente, programacionGuardada)
            if (programacionGuardada.activa && programacionGuardada.esAhora()) {
                val accionInicio =
                    if (programacionGuardada.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES"
                estadoPiscinaService.aplicarComando(piscinaId, accionInicio, programacionId)
            }
        } catch (e: Exception) {
            throw RuntimeException("Error al crear jobs de Quartz: ${e.message}", e)
        }
    }

    fun deleteProgramacion(piscinaId: Long, programacionId: Long, estadoPiscinaService: EstadoPiscinaService) {
        val piscina = piscinaService.findById(piscinaId)
        val programacion = piscina.programaciones.find { it.id == programacionId }
            ?: throw NotFoundException("Programación no encontrada")

        programacionHelper.eliminarJobs(programacion)

        piscina.eliminarProgramacionFiltrado(programacionId)
        piscina.eliminarProgramacionIluminacion(programacionId)
        piscinaRepository.save(piscina)

        if (programacion.activa && programacion.ejecutando && !programacion.pausadaManualmente) {
            val accionFin =
                if (programacion.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES"
            estadoPiscinaService.aplicarComando(piscinaId, accionFin, programacionId)
        }
    }

    fun validarSolapamientoProgramaciones(piscina: Piscina, nuevaProgramacion: Programacion) {
        if (nuevaProgramacion.tipo == ProgramacionType.FILTRADO) {
            val programacionesFiltrado = piscina.programaciones
                .filter { it.tipo == ProgramacionType.FILTRADO && it.activa && it.id != nuevaProgramacion.id }

            for (programacionExistente in programacionesFiltrado) {
                // Verificar si comparten días
                val diasComunes = programacionExistente.dias.intersect(nuevaProgramacion.dias.toSet())

                if (diasComunes.isNotEmpty()) {
                    // Verificar si los horarios se solapan
                    val seSuperponen = !(nuevaProgramacion.horaFin <= programacionExistente.horaInicio ||
                            nuevaProgramacion.horaInicio >= programacionExistente.horaFin)

                    if (seSuperponen) {
                        throw IllegalStateException(
                            "La programación se solapa con otra existente en los días: ${diasComunes.joinToString(", ")}"
                        )
                    }
                }
            }
        }

        if (nuevaProgramacion.tipo == ProgramacionType.ILUMINACION) {
            val programacionesIluminacion = piscina.programaciones
                .filter { it.tipo == ProgramacionType.ILUMINACION && it.activa && it.id != nuevaProgramacion.id }

            for (programacionExistente in programacionesIluminacion) {
                // Verificar si comparten días
                val diasComunes = programacionExistente.dias.intersect(nuevaProgramacion.dias.toSet())

                if (diasComunes.isNotEmpty()) {
                    // Verificar si los horarios se solapan
                    val seSuperponen = !(nuevaProgramacion.horaFin <= programacionExistente.horaInicio ||
                            nuevaProgramacion.horaInicio >= programacionExistente.horaFin)

                    if (seSuperponen) {
                        throw IllegalStateException(
                            "La programación se solapa con otra existente en los días: ${diasComunes.joinToString(", ")}"
                        )
                    }
                }
            }
        }
    }

    @Transactional
    fun desactivarProgramacion(
        piscinaId: Long,
        programacionId: Long,
        estadoPiscinaService: EstadoPiscinaService
    ) {
        val piscina = piscinaService.findById(piscinaId)
        val programacion = piscina.programaciones.find { it.id == programacionId }
            ?: throw NotFoundException("Programación no encontrada")

        // ✅ Evitar desactivar algo que ya está inactivo
        if (!programacion.activa) {
            println("Programación $programacionId ya está inactiva.")
            return
        }

        programacion.activa = false
        piscinaRepository.save(piscina)

        // 🔹 Eliminar jobs Quartz asociados
        programacionHelper.eliminarJobs(programacion)

        // 🔹 Si estaba ejecutando → enviar comando de fin
        if (programacion.ejecutando) {
            val comandoFin = if (programacion.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES"
            println("Programación $programacionId se desactiva durante ejecución, enviando $comandoFin")
            estadoPiscinaService.aplicarComando(piscinaId, comandoFin, programacionId)
        }

        println("Programación $programacionId desactivada y jobs Quartz eliminados.")
    }

    @Transactional
    fun activarProgramacion(
        piscinaId: Long,
        programacionId: Long,
        estadoPiscinaService: EstadoPiscinaService
    ) {
        val piscina = piscinaService.findById(piscinaId)
        val programacion = piscina.programaciones.find { it.id == programacionId }
            ?: throw NotFoundException("Programación no encontrada")
        val patente = piscina.plaqueta.patente

        // ✅ Evitar reactivar algo ya activo
        if (programacion.activa) {
            println("Programación $programacionId ya está activa.")
            return
        }

        programacion.activa = true
        programacion.pausadaManualmente = false
        piscinaRepository.save(piscina)

        // 🔹 Crear los jobs nuevamente
        programacionHelper.crearJobs(piscinaId, patente, programacion)

        // 🔹 Si el rango actual corresponde a una ejecución activa, arrancar ya
        if (programacion.esAhora()) {
            val comandoInicio = if (programacion.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES"
            println("Activando programación $programacionId en ejecución inmediata ($comandoInicio)")
            estadoPiscinaService.aplicarComando(piscinaId, comandoInicio, programacionId)
        }

        println("✅ Programación $programacionId activada y jobs Quartz recreados.")
    }

    fun updateProgramacion(piscinaId: Long, programacion: Programacion, estadoPiscinaService: EstadoPiscinaService) {
        val piscina = piscinaService.findById(piscinaId)
        val patente = piscina.plaqueta.patente

        val programacionExistente = piscina.programaciones.find { it.id == programacion.id }
            ?: throw NotFoundException("La programación con ID: ${programacion.id} no pertenece a la piscina con ID: $piscinaId")

        // 🔹 0. VALIDAR SOLAPAMIENTO PRIMERO
        validarSolapamientoProgramaciones(piscina, programacion)

        // 🔹 1. Guardar estado anterior
        val estabaEjecutando = programacionExistente.ejecutando

        // 🔹 2. Eliminar jobs anteriores si esta activa, si esta inactiva no hay jobs
        if (programacion.activa) {
            programacionHelper.eliminarJobs(programacionExistente)
        }

        // 🔹 3. Actualizar campos
        programacionExistente.apply {
            horaInicio = programacion.horaInicio
            horaFin = programacion.horaFin
            dias = programacion.dias
            activa = programacion.activa
        }

        // 🔹 4. Determinar si debería estar ejecutándose AHORA
        val deberiaEstarEjecutandoAhora = programacionExistente.activa && programacionExistente.esAhora() && !programacionExistente.pausadaManualmente

        // 🔹 5. Estaba ejecutando y ya NO debería → detener
        if (estabaEjecutando && !deberiaEstarEjecutandoAhora) {
            try {
                val accionFin =
                    if (programacionExistente.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES"
                estadoPiscinaService.aplicarComando(piscinaId, accionFin, programacion.id)
            } catch (e: Exception) {
                println("⚠️ Error al detener programación: ${e.message}")
            }
        }

        // 🔹 6. NO estaba ejecutando pero ahora SÍ debería → iniciar
        if (!estabaEjecutando && deberiaEstarEjecutandoAhora) {
            try {
                val accionInicio =
                    if (programacionExistente.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES"
                estadoPiscinaService.aplicarComando(piscinaId, accionInicio, programacion.id)
            } catch (e: Exception) {
                println("Error al iniciar programación: ${e.message}")
            }
        }

        // 🔹 7. Si está activa, crear nuevos jobs. Si está inactiva no hay jobs
        if (programacionExistente.activa) {
            programacionHelper.crearJobs(piscinaId, patente, programacionExistente)
        }

        piscinaRepository.save(piscina)
    }
}