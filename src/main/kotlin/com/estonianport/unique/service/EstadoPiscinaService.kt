package com.estonianport.unique.service

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.mqtt.MqttPublisherService
import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.estonianport.unique.repository.EstadoPiscinaRepository
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service

@Service
class EstadoPiscinaService(
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val piscinaRepository: PiscinaRepository,
    private val piscinaService: PiscinaService,
    private val mqttPublisherService: MqttPublisherService
) {

    fun findEstadoActualByPiscinaId(piscinaId: Long): EstadoPiscina {
        return estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId) ?: EstadoPiscina.estadoInicial(
            piscinaService.findById(piscinaId)
        )
    }

    fun updateEntradaAgua(piscinaId: Long, entradaAgua: MutableList<EntradaAguaType>) {
        val patentePlaqueta = piscinaService.getPatentePlaqueta(piscinaId)
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
        //2. si se activa una funcion de filtro, ahi se envian los comandos con la nueva entrada de agua desde  updateFuncionActiva()
        val piscina = piscinaService.findById(piscinaId)
        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun desactivarFuncionActiva(piscinaId: Long, patente: String) {
        val estadoActualPiscina = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
            desactivarFuncionFiltro()
        }
        enviarComandosFiltroDesactivado(nuevoEstadoPiscina, patente)

        val piscina = piscinaService.findById(piscinaId)
        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun updateFuncionActiva(piscinaId: Long, funcionActiva: FuncionFiltroType, patente: String) {
        val estadoActualPiscina = findEstadoActualByPiscinaId(piscinaId)
        val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
            activarFuncionFiltro(funcionActiva)
        }
        enviarComandosFiltroActivado(nuevoEstadoPiscina, patente)

        val piscina = piscinaService.findById(piscinaId)
        persistirNuevoEstado(piscina, nuevoEstadoPiscina)
    }

    fun enviarComandosFiltroActivado(estadoPiscina: EstadoPiscina, patente: String) {
        if (estadoPiscina.funcionFiltroActivo == FuncionFiltroType.FILTRAR || estadoPiscina.funcionFiltroActivo == FuncionFiltroType.RECIRCULAR) {
            mqttPublisherService.sendCommandList(
                patente,
                estadoPiscina.entradaAguaActiva,
                estadoPiscina.funcionFiltroActivo,
                estadoPiscina.sistemasGermicida.map { SistemaGermicidaType.valueOf(it.tipo()) })
        } else {
            mqttPublisherService.sendCommandList(
                patente,
                estadoPiscina.entradaAguaActiva,
                estadoPiscina.funcionFiltroActivo,
                listOf()
            )
        }
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
        val piscina = piscinaService.findById(piscinaId)
        val patentePlaqueta = piscina.plaqueta.patente
        val estadoActualPiscina = estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)
            ?: throw NotFoundException("Estado de piscina no encontrado con ID: $piscinaId")

        if (estadoActualPiscina.lucesManuales) {
            //mqttPublisherService.sendCommand(patentePlaqueta, "desactivar_luces")

            val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
                lucesManuales = false
            }

            println("Simulando envío de comando MQTT: desactivar_luces a la plaqueta con patente $patentePlaqueta")

            persistirNuevoEstado(piscina, nuevoEstadoPiscina)
        }
    }

    fun encenderLuces(piscinaId: Long) {
        val piscina = piscinaService.findById(piscinaId)
        val patentePlaqueta = piscina.plaqueta.patente
        val estadoActualPiscina = estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)
            ?: throw NotFoundException("Estado de piscina no encontrado con ID: $piscinaId")

        if (!estadoActualPiscina.lucesManuales) {
            //mqttPublisherService.sendCommand(patentePlaqueta, "activar_luces")

            val nuevoEstadoPiscina = estadoActualPiscina.copy().apply {
                lucesManuales = true
            }

            println("Simulando envío de comando MQTT: activar_luces a la plaqueta con patente $patentePlaqueta")

            persistirNuevoEstado(piscina, nuevoEstadoPiscina)
        }
    }

    fun persistirNuevoEstado (piscina: Piscina, nuevoEstado: EstadoPiscina) {
        piscina.agregarNuevoEstadoPiscina(nuevoEstado)
        piscinaRepository.save(piscina)
    }

}