package com.estonianport.unique.common.mqtt

import com.estonianport.unique.dto.response.LecturaResponseDto
import com.estonianport.unique.dto.response.PiscinaEstadoDto
import com.estonianport.unique.model.*
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.repository.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service

@Service
class MqttSubscriberService(
    mqttClient: MqttClient,
    private val plaquetaRepository: PlaquetaRepository,
    private val lecturaRepository: LecturaRepository,
    private val piscinaRepository: PiscinaRepository,
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val mqttPublisherService : MqttPublisherService
) {

    private val mqttListener = IMqttMessageListener { topic, message ->
        val payload = String(message.payload)
        println("Mensaje recibido en $topic: $payload")

        when {
            topic == "plaquetas/registro" -> procesarRegistro(payload)

            topic.matches(Regex("plaquetas/.+/lectura")) -> {
                val patente = extraerPatente(topic, "lectura")
                patente?.let { procesarLectura(payload, it) }
            }

            topic.matches(Regex("plaquetas/.+/estado")) -> {
                val patente = extraerPatente(topic, "estado")
                patente?.let { procesarEstado(payload, it) }
            }
        }
    }

    private fun procesarRegistro(payload: String) {
        val patente = extractPatente(payload)
        val plaqueta = plaquetaRepository.findByPatenteAndEstado(patente, EstadoType.PENDIENTE)
        if (plaqueta != null) {
            plaqueta.estado = EstadoType.ACTIVO
            plaquetaRepository.save(plaqueta)
            println("Plaqueta $patente activada")
            mqttPublisherService.sendCommand(plaqueta.patente,"confirmacion_activacion")
        }
    }

    private fun procesarLectura(payload: String, patente: String) {
        try {
            val mapper = jacksonObjectMapper()
            val dto = mapper.readValue(payload, LecturaResponseDto::class.java)

            val piscina = piscinaRepository.findByPlaqueta(
                plaquetaRepository.findByPatenteAndEstado(patente, EstadoType.ACTIVO)!!
            )

            val lectura = Lectura(
                piscina = piscina,
                redox = dto.redox,
                ph = dto.ph,
                cloro = dto.cloro,
                presion = dto.presion,
                temperatura = dto.temperatura,
                humedad = dto.humedad,
                temperaturaAgua = dto.temperaturaAgua,
            )

            lecturaRepository.save(lectura)
            println("Lectura guardada correctamente: $lectura")
        } catch (ex: Exception) {
            println("Error parseando lectura: $payload")
        }
    }

    private fun procesarEstado(payload: String, patente: String) {
        try {
            val mapper = jacksonObjectMapper()
            val dto = mapper.readValue(payload, PiscinaEstadoDto::class.java)

            val piscina = piscinaRepository.findByPlaqueta(
                plaquetaRepository.findByPatenteAndEstado(patente, EstadoType.ACTIVO)!!
            )

            val estado = EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = dto.entradaAguaActiva,
                sistemaGermicidaActivo = dto.sistemaGermicidaActivo,
                funcionFiltroActivo = dto.funcionFiltroActivo,
                calefaccionActiva = dto.calefaccionActiva
            )

            estadoPiscinaRepository.save(estado)
            println("Estado guardado correctamente: $estado")
        } catch (ex: Exception) {
            println("Error parseando estado: $payload")
        }
    }

    private fun extraerPatente(topic: String, tipo: String): String? {
        val regex = Regex("plaquetas/(.+)/$tipo")
        return regex.find(topic)?.groups?.get(1)?.value
    }

    private fun extractPatente(payload: String): String {
        return Regex("\"patente\":\\s*\"(\\w{4})\"")
            .find(payload)?.groups?.get(1)?.value ?: "UNKNOWN"
    }

    init {
        mqttClient.subscribe("plaquetas/registro", mqttListener)
        mqttClient.subscribe("plaquetas/+/lectura", mqttListener)
        mqttClient.subscribe("plaquetas/+/estado", mqttListener)
        println("Suscrito a topics de plaquetas")
    }
}
