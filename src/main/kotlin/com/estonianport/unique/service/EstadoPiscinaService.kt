package com.estonianport.unique.service

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.mqtt.MqttPublisherService
import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.estonianport.unique.repository.EstadoPiscinaRepository
import org.springframework.stereotype.Service

@Service
class EstadoPiscinaService(
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
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
        val estadoPiscina = findEstadoActualByPiscinaId(piscinaId)
        estadoPiscina.entradaAguaActiva.clear()
        estadoPiscina.entradaAguaActiva.addAll(entradaAgua)
        if (estadoPiscina.funcionFiltroActivo != FuncionFiltroType.REPOSO) {
            enviarComandosFiltroActivado(estadoPiscina, patentePlaqueta)
        }
        estadoPiscinaRepository.save(estadoPiscina)
    }

    fun desactivarFuncionActiva(piscinaId: Long, patente: String) {
        val estadoPiscina = findEstadoActualByPiscinaId(piscinaId)
        estadoPiscina.desactivarFuncionFiltro()
        enviarComandosFiltroDesactivado(estadoPiscina, patente)
        estadoPiscinaRepository.save(estadoPiscina)
    }

    fun updateFuncionActiva(piscinaId: Long, funcionActiva: FuncionFiltroType, patente: String) {
        val estadoPiscina = findEstadoActualByPiscinaId(piscinaId)
        estadoPiscina.activarFuncionFiltro(funcionActiva)
        enviarComandosFiltroActivado(estadoPiscina, patente)
        estadoPiscinaRepository.save(estadoPiscina)
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
        val patentePlaqueta = piscinaService.getPatentePlaqueta(piscinaId)
        val estadoPiscina = estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)
            ?: throw NotFoundException("Estado de piscina no encontrado con ID: $piscinaId")
        if (estadoPiscina.lucesManuales) {
            //mqttPublisherService.sendCommand(patentePlaqueta, "desactivar_luces")
            println("Simulando envío de comando MQTT: desactivar_luces a la plaqueta con patente $patentePlaqueta")
            estadoPiscina.lucesManuales = false
            estadoPiscinaRepository.save(estadoPiscina)
        }
    }

    fun encenderLuces(piscinaId: Long) {
        val patentePlaqueta = piscinaService.getPatentePlaqueta(piscinaId)
        val estadoPiscina = estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)
            ?: throw NotFoundException("Estado de piscina no encontrado con ID: $piscinaId")
        if (!estadoPiscina.lucesManuales) {
            //mqttPublisherService.sendCommand(patentePlaqueta, "activar_luces")
            println("Simulando envío de comando MQTT: activar_luces a la plaqueta con patente $patentePlaqueta")
            estadoPiscina.lucesManuales = true
            estadoPiscinaRepository.save(estadoPiscina)
        }
    }

}