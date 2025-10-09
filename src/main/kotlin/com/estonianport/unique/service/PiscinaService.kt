package com.estonianport.unique.service

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.quartz.QuartzSchedulerService
import com.estonianport.unique.model.Bomba
import com.estonianport.unique.model.Calefaccion
import com.estonianport.unique.model.Filtro
import com.estonianport.unique.model.Ionizador
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Programacion
import com.estonianport.unique.model.Registro
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.Trasductor
import com.estonianport.unique.model.UV
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
class PiscinaService(
    private val piscinaRepository: PiscinaRepository,
    private val usuarioService: UsuarioService,
    private val quartzSchedulerService: QuartzSchedulerService
) {

    fun getPiscinasByUsuarioId(usuarioId: Long): List<Piscina> {
        usuarioService.findById(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")
        return piscinaRepository.findByAdministradorId(usuarioId)
    }

    fun findById(piscinaId: Long): Piscina {
        return piscinaRepository.findById(piscinaId)
            ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
    }

    fun getLecturasPiscina(piscinaId: Long): List<LecturaConErrorResponseDto> {
        val resultados = piscinaRepository.findTodasLecturasConError(piscinaId)
        return resultados.map {
            LecturaConErrorResponseDto(
                id = (it[0] as Number).toLong(),
                fecha = (it[1] as Timestamp).toLocalDateTime().toString(),
                ph = (it[2] as? Number)?.toDouble(),
                cloro = (it[3] as? Number)?.toDouble(),
                redox = (it[4] as? Number)?.toDouble(),
                presion = (it[5] as? Number)?.toDouble(),
                esError = it[6] as Boolean
            )

        }
    }

    fun getPresion(piscinaId: Long): Double {
        return piscinaRepository.getPresion(piscinaId)
            ?: 0.0
    }

    fun getPh(piscinaId: Long): Double {
        return piscinaRepository.getPh(piscinaId)
            ?: 0.0
    }

    fun getDiferenciaPh(piscinaId: Long): Double {
        val ultimasDosPh = piscinaRepository.getDiferenciaPh(piscinaId)
            ?: return 0.0

        if (ultimasDosPh.size < 2) {
            return 0.0
        }
        return ultimasDosPh[0] - ultimasDosPh[1]
    }

    fun totalPiscinas(): Int {
        return piscinaRepository.count().toInt()
    }

    fun countPiscinasDesborde(): Int {
        return piscinaRepository.countDesbordante()
    }

    fun countPiscinasSkimmer(): Int {
        return piscinaRepository.countSkimmer()
    }

    fun getVolumenTotal(): Double {
        return piscinaRepository.getTotalVolumen()
    }

    fun getVolumenPromedio(): Double {
        return piscinaRepository.getPromedioVolumen()
    }

    fun getPiscinasRegistradas(): List<Piscina> {
        return piscinaRepository.findAll()
    }

    fun getPiscinasSinAdministrador(): List<Piscina> {
        return piscinaRepository.findByAdministradorIsNull()
    }

    fun desvincularAdministrador(usuarioId: Long, piscinaId: Long) {
        val piscina = findById(piscinaId)
        piscina.administrador = null
        piscinaRepository.save(piscina)
        usuarioService.desvincularPiscina(usuarioId, tienePiscinaAsignada(usuarioId))
    }

    fun tienePiscinaAsignada(usuarioId: Long): Boolean {
        return piscinaRepository.existsByAdministradorId(usuarioId)
    }


    fun create(piscina: Piscina): Piscina {
        return piscinaRepository.save(piscina)
    }

    fun deleteProgramacion(piscinaId: Long, programacionId: Long) {
        val piscina = findById(piscinaId)
        val programacion = piscina.programacionFiltrado.find { it.id == programacionId }
            ?: piscina.programacionIluminacion.find { it.id == programacionId }
            ?: throw NotFoundException("Programación no encontrada")

        programacion.dias.forEach { dia ->
            quartzSchedulerService.eliminarJob("inicio_${programacionId}_${dia.name}")
            quartzSchedulerService.eliminarJob("fin_${programacionId}_${dia.name}")
        }

        piscina.eliminarProgramacionFiltrado(programacionId)
        piscina.eliminarProgramacionFiltrado(programacionId)
        piscinaRepository.save(piscina)
    }


    fun agregarProgramacion(piscinaId: Long, nuevaProgramacion: Programacion) {
        val piscina = findById(piscinaId)
        val patente = piscina.plaqueta.patente

        if (nuevaProgramacion.tipo == ProgramacionType.FILTRADO) {
            piscina.agregarProgramacionFiltrado(nuevaProgramacion)
        } else {
            piscina.agregarProgramacionIluminacion(nuevaProgramacion)
        }

        val guardada = piscinaRepository.save(piscina)
        val programacionId = guardada.id!!

        // Crear jobs Quartz por cada día configurado
        nuevaProgramacion.dias.forEach { dia ->
            quartzSchedulerService.programarJob(
                piscinaId,
                patente,
                if (nuevaProgramacion.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES",
                dia,
                nuevaProgramacion.horaInicio,
                "inicio_${programacionId}_${dia.name}"
            )
            quartzSchedulerService.programarJob(
                piscinaId,
                patente,
                if (nuevaProgramacion.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES",
                dia,
                nuevaProgramacion.horaFin,
                "fin_${programacionId}_${dia.name}"
            )
        }
    }


    fun updateProgramacion(piscinaId: Long, programacion: Programacion) {
        val piscina = findById(piscinaId)
        val patente = piscina.plaqueta.patente

        val programacionExistente = (
                piscina.programacionFiltrado.find { it.id == programacion.id } ?:
                piscina.programacionIluminacion.find { it.id == programacion.id }
                ) ?: throw NotFoundException("La programación con ID: ${programacion.id} no pertenece a la piscina con ID: $piscinaId")

        // 🔹 1. Eliminar jobs anteriores
        programacionExistente.dias.forEach { dia ->
            quartzSchedulerService.eliminarJob("inicio_${programacion.id}_${dia.name}")
            quartzSchedulerService.eliminarJob("fin_${programacion.id}_${dia.name}")
        }

        // 🔹 2. Actualizar campos
        programacionExistente.apply {
            horaInicio = programacion.horaInicio
            horaFin = programacion.horaFin
            dias = programacion.dias
            activa = programacion.activa
        }

        // 🔹 3. Si está activa, crear nuevos jobs
        if (programacion.activa) {
            programacion.dias.forEach { dia ->
                quartzSchedulerService.programarJob(
                    piscinaId,
                    patente,
                    if (programacion.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES",
                    dia,
                    programacion.horaInicio,
                    "inicio_${programacion.id}_${dia.name}"
                )

                quartzSchedulerService.programarJob(
                    piscinaId,
                    patente,
                    if (programacion.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES",
                    dia,
                    programacion.horaFin,
                    "fin_${programacion.id}_${dia.name}"
                )
            }

            // 🔹 4. Calcular próxima ejecución visible para el front
            programacionExistente.proximaEjecucion = programacion.dias.minOfOrNull {
                quartzSchedulerService.calcularProximaEjecucion(it, programacion.horaInicio)
            }
        } else {
            // Si la desactivás, limpiamos el campo proxima_ejecucion
            programacionExistente.proximaEjecucion = null
        }

        piscinaRepository.save(piscina)
    }


    fun asignarAdministrador(usuarioId: Long, piscinaId: Long) {
        val usuario =
            usuarioService.findById(usuarioId) ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")
        val piscina = findById(piscinaId)
        piscina.administrador = usuario
        usuarioService.piscinaAsignada(usuario)
        piscinaRepository.save(piscina)
    }

    fun usuarioEliminado(usuarioId: Long) {
        val piscinas = getPiscinasByUsuarioId(usuarioId)
        piscinas.forEach {
            it.administrador = null
            piscinaRepository.save(it)
        }
    }

    fun updateBomba(piscinaId: Long, bombaActualizada: Bomba) {
        val piscina = findById(piscinaId)
        piscina.bomba.find { it.id == bombaActualizada.id }?.apply {
            marca = bombaActualizada.marca
            modelo = bombaActualizada.modelo
            potencia = bombaActualizada.potencia
            activa = bombaActualizada.activa
        }
        piscinaRepository.save(piscina)
    }

    fun obtenerProximaEjecucionPiscina(piscinaId: Long): LocalDateTime? {
        val piscina = findById(piscinaId)
        return piscina.programacionFiltrado
            .filter { it.activa && it.proximaEjecucion != null }
            .minOfOrNull { it.proximaEjecucion!! }
    }


    fun updateFiltro(piscinaId: Long, filtroActualizado: Filtro) {
        val piscina = findById(piscinaId)
        if (piscina.filtro.id != filtroActualizado.id) {
            throw NotFoundException("El filtro con ID: ${filtroActualizado.id} no pertenece a la piscina con ID: $piscinaId")
        }
        piscina.filtro = filtroActualizado //REVISAR ESTO!
        piscinaRepository.save(piscina)
    }

    fun updateGermicida(piscinaId: Long, germicidaActualizada: SistemaGermicida) {
        val piscina = findById(piscinaId)
        val germicida = piscina.sistemaGermicida.find { it.id == germicidaActualizada.id }
            ?: throw NotFoundException("El sistema germicida con ID: ${germicidaActualizada.id} no pertenece a la piscina con ID: $piscinaId")
        germicida.apply {
            marca = germicidaActualizada.marca
        }
        if (germicidaActualizada is UV && germicida is UV) {
            germicida.potencia = germicidaActualizada.potencia
        }
        if (germicidaActualizada is Ionizador && germicida is Ionizador) {
            germicida.electrodos = germicidaActualizada.electrodos
        }
        if (germicidaActualizada is Trasductor && germicida is Trasductor) {
            germicida.potencia = germicidaActualizada.potencia
        }
        piscinaRepository.save(piscina)
    }

    fun updateCalefaccion(piscinaId: Long, calefaccionActualizada: Calefaccion) {
        val piscina = findById(piscinaId)
        piscina.calefaccion?.apply {
            this.modelo = calefaccionActualizada.modelo
            this.marca = calefaccionActualizada.marca
            this.potencia = calefaccionActualizada.potencia
        } ?: throw NotFoundException("La piscina con ID: $piscinaId no tiene calefacción asignada")
        piscinaRepository.save(piscina)
    }

    fun deleteCalefaccion(piscinaId: Long) {
        val piscina = findById(piscinaId)
        if (piscina.calefaccion == null) {
            throw NotFoundException("La piscina con ID: $piscinaId no tiene calefacción asignada")
        }
        piscina.calefaccion = null
        piscinaRepository.save(piscina)
    }

    fun deleteGermicida(piscinaId: Long, germicidaId: Long) {
        val piscina = findById(piscinaId)
        val germicida = piscina.sistemaGermicida.find { it.id == germicidaId }
            ?: throw NotFoundException("El sistema germicida con ID: $germicidaId no pertenece a la piscina con ID: $piscinaId")
        piscina.sistemaGermicida.remove(germicida)
        piscinaRepository.save(piscina)
    }

    fun addBomba(piscinaId: Long, bomba: Bomba) {
        val piscina = findById(piscinaId)
        if (piscina.bomba.size >= 3) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene el máximo de bombas permitidas (3)")
        }
        piscina.bomba.add(bomba)
        piscinaRepository.save(piscina)
    }

    fun addGermicida(piscinaId: Long, germicida: SistemaGermicida) {
        val piscina = findById(piscinaId)
        if (piscina.sistemaGermicida.size >= 3) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene el máximo de sistemas germicidas permitidos (3)")
        }
        validarUnicidadGermicida(germicida, piscina)
        piscina.sistemaGermicida.add(germicida)
        piscinaRepository.save(piscina)
    }

    fun addCalefaccion(piscinaId: Long, calefaccion: Calefaccion) {
        val piscina = findById(piscinaId)
        if (piscina.calefaccion != null) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene una calefacción asignada")
        }
        piscina.calefaccion = calefaccion
        piscinaRepository.save(piscina)
    }

    fun validarUnicidadGermicida(germicida: SistemaGermicida, piscina: Piscina) {
        if (germicida is UV) {
            if (piscina.sistemaGermicida.any { it is UV }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida UV asignado")
            }
        }
        if (germicida is Ionizador) {
            if (piscina.sistemaGermicida.any { it is Ionizador }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida Ionizador asignado")
            }
        }
        if (germicida is Trasductor) {
            if (piscina.sistemaGermicida.any { it is Trasductor }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida Trasductor asignado")
            }
        }
    }

    fun updateCompuestos(piscinaId: Long, orp: Boolean, controlPh: Boolean, cloroSalino: Boolean) {
        val piscina = findById(piscinaId)
        piscina.orp = orp
        piscina.controlAutomaticoPH = controlPh
        piscina.cloroSalino = cloroSalino
        piscinaRepository.save(piscina)
    }

    fun addRegistro(piscinaId: Long, registro: Registro) {
        val piscina = findById(piscinaId)
        piscina.agregarRegistro(registro)
        piscinaRepository.save(piscina)
    }

    fun updateRegistro(piscinaId: Long, registro: Registro) {
        val piscina = findById(piscinaId)
        val registroExistente = piscina.registros.find { it.id == registro.id }
            ?: throw NotFoundException("El registro con ID: ${registro.id} no pertenece a la piscina con ID: $piscinaId")
        registroExistente.apply {
            fecha = registro.fecha
            dispositivo = registro.dispositivo
            accion = registro.accion
            descripcion = registro.descripcion
            nombreTecnico = registro.nombreTecnico
        }
        piscinaRepository.save(piscina)
    }

    fun deleteRegistro(piscinaId: Long, registroId: Long) {
        val piscina = findById(piscinaId)
        val registroExistente = piscina.registros.find { it.id == registroId }
            ?: throw NotFoundException("El registro con ID: $registroId no pertenece a la piscina con ID: $piscinaId")
        piscina.eliminarRegistro(registroExistente)
        piscinaRepository.save(piscina)
    }

    fun getPatentePlaqueta(piscinaId: Long): String {
        val piscina = findById(piscinaId)
        return piscina.plaqueta.patente
    }

}