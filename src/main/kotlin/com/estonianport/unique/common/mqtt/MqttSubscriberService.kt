package com.estonianport.unique.common.mqtt

import com.estonianport.unique.dto.response.LecturaResponseDto
import com.estonianport.unique.dto.response.PiscinaEstadoDto
import com.estonianport.unique.model.*
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.repository.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import jakarta.annotation.PostConstruct // Use PostConstruct for initialization after dependency injection
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class MqttSubscriberService(
    private val mqttClient: Mqtt5AsyncClient, // Inyectamos el cliente de HiveMQ
    private val plaquetaRepository: PlaquetaRepository,
    private val lecturaRepository: LecturaRepository,
    private val piscinaRepository: PiscinaRepository,
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val mqttPublisherService: MqttPublisherService
) {
    // The message handling logic is now inside a main callback function
    private fun handleMqttMessage(publish: Mqtt5Publish) {
        val topic = publish.topic.toString()
        val payload = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
        println("Mensaje recibido en $topic: $payload")

        when (topic) {
            "plaquetas/registro" -> procesarRegistro(payload)
            "plaquetas/lectura" -> procesarLectura(payload)
            "plaquetas/estado" -> procesarEstado(payload)
        }
    }

    // Use @PostConstruct to subscribe once the service is fully initialized
    @PostConstruct
    fun subscribeToTopics() {
        // We subscribe to multiple topics using the same callback handler
        val topics = listOf("plaquetas/registro", "plaquetas/lectura", "plaquetas/estado")

        topics.forEach { topic ->
            mqttClient.subscribeWith()
                .topicFilter(topic)
                .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE)
                .callback(this::handleMqttMessage) // Set the handler function
                .send()
                .whenComplete { subAck, throwable ->
                    if (throwable != null) {
                        println("Failed to subscribe to $topic: ${throwable.message}")
                    } else {
                        println("Suscrito a topic $topic")
                    }
                }
        }
        println("Suscripción iniciada para topics de plaquetas")
    }


    private fun procesarRegistro(payload: String) {
        val patente = extractPatente(payload)
        val plaqueta = plaquetaRepository.findByPatenteAndEstado(patente, EstadoType.PENDIENTE)
        if (plaqueta != null) {
            plaqueta.estado = EstadoType.ACTIVO
            plaquetaRepository.save(plaqueta)
            println("Plaqueta $patente activada")
            mqttPublisherService.sendCommand(plaqueta.patente, "confirmacion_activacion")
        }
    }

    private fun procesarLectura(payload: String) {
        try {
            val mapper = jacksonObjectMapper()
            val dto = mapper.readValue(payload, LecturaResponseDto::class.java)

            val piscina = piscinaRepository.findByPlaqueta(
                plaquetaRepository.findByPatenteAndEstado(dto.patente, EstadoType.ACTIVO)!!
            )

            val lectura = Lectura(
                piscina = piscina,
                redox = dto.redox,
                ph = dto.ph,
                cloro = dto.cloro,
                presion = dto.presion,
                temperaturaAgua = dto.temperaturaAgua,
            )

            lecturaRepository.save(lectura)
            println("Lectura guardada correctamente: $lectura")
        } catch (ex: Exception) {
            println("Error parseando lectura: $payload - ${ex.message}")
        }
    }

    private fun procesarEstado(payload: String) {
        try {
            val mapper = jacksonObjectMapper()
            val dto = mapper.readValue(payload, PiscinaEstadoDto::class.java)

            val piscina = piscinaRepository.findByPlaqueta(
                plaquetaRepository.findByPatenteAndEstado(dto.patente, EstadoType.ACTIVO)!!
            )

            val estado = EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = dto.entradaAguaActiva.toMutableList(),
                funcionFiltroActivo = dto.funcionFiltroActivo,
            )

            estadoPiscinaRepository.save(estado)
            println("Estado guardado correctamente: $estado")
        } catch (ex: Exception) {
            println("Error parseando estado: $payload - ${ex.message}")
        }
    }

    private fun extractPatente(payload: String): String {
        return Regex("\"patente\":\\s*\"(\\w{4})\"")
            .find(payload)?.groups?.get(1)?.value ?: "UNKNOWN"
    }
}