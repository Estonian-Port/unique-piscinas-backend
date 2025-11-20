package com.estonianport.unique.service

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.mqtt.MqttPublisherService
import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.estonianport.unique.repository.EstadoPiscinaRepository
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EstadoPiscinaService(
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val piscinaRepository: PiscinaRepository,
    private val mqttPublisherService: MqttPublisherService
) {

    fun findEstadoActualByPiscinaId(piscinaId: Long): EstadoPiscina {
        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")

        return estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)
            ?: EstadoPiscina.estadoInicial(piscina)
    }

    fun updateEntradaAgua(piscinaId: Long, entradaAgua: MutableList<EntradaAguaType>) {
        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        val patentePlaqueta = piscina.plaqueta.patente
        val estadoActualPiscina = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
            entradaAguaActiva.clear()
            entradaAguaActiva.addAll(entradaAgua)
        }
        if (nuevoEstadoPiscina.funcionFiltroActivo != FuncionFiltroType.REPOSO) { //es decir se agrega una entrada de agua
            enviarComandosFiltroActivado(nuevoEstadoPiscina, patentePlaqueta)
        }

        //si esta en reposo no se envian comandos porque no hay filtro activo y pueden pasar dos cosas
        //1. si pasan los 30 segundos y no se activo el filtro, se limpia la entrada de agua automaticamente
        //2. si se activa una funcion de filtro, ahi se envian los comandos con la nueva entrada de agua desde updateFuncionActiva()
        println("Piscina ID: $piscinaId")

        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun desactivarFuncionActiva(piscinaId: Long, patente: String) {
        val estadoActualPiscina = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
            desactivarFuncionFiltro()
        }
        enviarComandosFiltroDesactivado(nuevoEstadoPiscina, patente)

        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun updateFuncionActiva(piscinaId: Long, funcionActiva: FuncionFiltroType, patente: String) {
        val estadoActualPiscina = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
            activarFuncionFiltro(funcionActiva)
        }
        enviarComandosFiltroActivado(nuevoEstadoPiscina, patente)

        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun enviarComandosFiltroActivado(estadoPiscina: EstadoPiscina, patente: String) {
        println("Enviando comandos MQTT para filtro activado a la plaqueta con patente $patente")
//        if (estadoPiscina.funcionFiltroActivo == FuncionFiltroType.FILTRAR || estadoPiscina.funcionFiltroActivo == FuncionFiltroType.RECIRCULAR) {
//            mqttPublisherService.sendCommandList(
//                patente,
//                estadoPiscina.entradaAguaActiva,
//                estadoPiscina.funcionFiltroActivo,
//                estadoPiscina.sistemasGermicida.map { SistemaGermicidaType.valueOf(it.tipo()) })
//        } else {
//            mqttPublisherService.sendCommandList(
//                patente,
//                estadoPiscina.entradaAguaActiva,
//                estadoPiscina.funcionFiltroActivo,
//                listOf()
//            )
//        }
    }

    fun enviarComandosFiltroDesactivado(estadoPiscina: EstadoPiscina, patente: String) {
        mqttPublisherService.sendCommandList(
            patente,
            estadoPiscina.entradaAguaActiva,
            estadoPiscina.funcionFiltroActivo,
            listOf()
        )
    }

    fun apagarLuces(piscinaId: Long) {
        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        val patente = piscina.plaqueta.patente

        // 1. Apagar luces físicamente
        mqttPublisherService.sendCommand(patente, "desactivar_luces")

        // 2. Pausar TODAS las programaciones de iluminación que estén ejecutándose
        piscina.programaciones
            .filter {
                it.tipo == ProgramacionType.ILUMINACION &&
                        it.activa &&
                        it.esAhora()
            }
            .forEach { it.pausadaManualmente = true }

        // 3. Actualizar estado
        val estadoActual = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstado = estadoActual.copy().apply {
            luces = false
        }
        persistirNuevoEstado(piscina, nuevoEstado)
    }

    fun encenderLuces(piscinaId: Long) {
        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        val patente = piscina.plaqueta.patente

        // 1. Encender luces físicamente
        mqttPublisherService.sendCommand(patente, "activar_luces")

        // 2. Actualizar estado
        val estadoActual = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstado = estadoActual.copy().apply {
            luces = true
        }
        persistirNuevoEstado(piscina, nuevoEstado)

        piscinaRepository.save(piscina)
    }

    fun persistirNuevoEstado(piscina: Piscina, nuevoEstado: EstadoPiscina) {
        piscina.agregarNuevoEstadoPiscina(nuevoEstado)
        piscina.verificarEstados()
        piscinaRepository.save(piscina)
    }

    @Transactional
    fun aplicarComando(piscinaId: Long, comando: String, programacionId: Long?) {
        val piscina =
            piscinaRepository.findById(piscinaId) ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
        val programacion = piscina.programaciones.find { it.id == programacionId }

        when (comando.uppercase()) {
            "ENCENDER_LUCES" -> {
                val estadoActual = findEstadoActualByPiscinaId(piscinaId)

                // 🔹 Si las luces ya están encendidas, solo marcar que la programación está activa
                if (!estadoActual.luces) {
                    encenderLuces(piscinaId)
                } else {
                    println("💡 Las luces ya estaban encendidas, programación toma control")
                }

                // 🔹 Asegurarse de que NO esté pausada (la programación ahora tiene control)
                programacion?.pausadaManualmente = false
                if (programacion != null) {
                    piscinaRepository.save(piscina)
                }
            }

            "APAGAR_LUCES" -> {
                apagarLuces(piscinaId)
                programacion?.pausadaManualmente = false
                if (programacion != null) {
                    piscinaRepository.save(piscina)
                }
            }

            "FILTRAR" -> {
                updateEntradaAgua(piscinaId, mutableListOf(EntradaAguaType.FONDO))
                updateFuncionActiva(piscinaId, FuncionFiltroType.FILTRAR, piscina.plaqueta.patente)
                programacion?.pausadaManualmente = false
            }

            "REPOSO" -> {
                updateEntradaAgua(piscinaId, mutableListOf())
                desactivarFuncionActiva(piscinaId, piscina.plaqueta.patente)
                programacion?.pausadaManualmente = false
            }
        }
        piscinaRepository.save(piscina)
    }

}