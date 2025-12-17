/*
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
    private val mqttPublisherService: MqttPublisherService
) {

    private val mqttListener = IMqttMessageListener { topic, message ->
        val payload = String(message.payload)
        println("Mensaje recibido en $topic: $payload")

        when (topic) {
            "plaquetas/registro" -> procesarRegistro(payload)
            "plaquetas/lectura" -> procesarLectura(payload)
            "plaquetas/estado" -> procesarEstado(payload)
        }
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

    init {
        mqttClient.subscribe("plaquetas/registro", mqttListener)
        mqttClient.subscribe("plaquetas/lectura", mqttListener)
        mqttClient.subscribe("plaquetas/estado", mqttListener)
        println("Suscrito a topics de plaquetas")
    }
}*/


package com.estonianport.unique.common.mqtt

import com.estonianport.unique.dto.response.LecturaResponseDto
import com.estonianport.unique.dto.response.PiscinaEstadoDto
import com.estonianport.unique.model.*
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.repository.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.annotation.PostConstruct
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MqttSubscriberService(
    private val mqttClient: MqttClient,
    private val mqttConnectOptions: MqttConnectOptions,
    private val plaquetaRepository: PlaquetaRepository,
    private val lecturaRepository: LecturaRepository,
    private val piscinaRepository: PiscinaRepository,
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val mqttPublisherService: MqttPublisherService
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()

    private val mqttListener = IMqttMessageListener { topic, message ->
        val payload = String(message.payload)
        logger.info("📥 Mensaje recibido en $topic: $payload")

        when (topic) {
            "plaquetas/registro" -> procesarRegistro(payload)
            "plaquetas/lectura" -> procesarLectura(payload)
            "plaquetas/estado" -> procesarEstado(payload)
        }
    }

    @PostConstruct
    fun init() {
        try {
            if (!mqttClient.isConnected) {
                mqttClient.connect(mqttConnectOptions)
                logger.info("✅ Conectado a MQTT (subscriber)")
            }

            mqttClient.subscribe("plaquetas/registro", 1, mqttListener)
            mqttClient.subscribe("plaquetas/lectura", 1, mqttListener)
            mqttClient.subscribe("plaquetas/estado", 1, mqttListener)

            logger.info("📡 Suscripto a topics de plaquetas")

        } catch (ex: Exception) {
            logger.warn("⚠️ No se pudo iniciar subscriber MQTT: ${ex.message}")
            // MUY IMPORTANTE: no relanzar
        }
    }

    // ====== TU LÓGICA (NO CAMBIA) ======

    private fun procesarRegistro(payload: String) {
        val patente = extractPatente(payload)
        val plaqueta = plaquetaRepository.findByPatenteAndEstado(patente, EstadoType.PENDIENTE)
        if (plaqueta != null) {
            plaqueta.estado = EstadoType.ACTIVO
            plaquetaRepository.save(plaqueta)
            mqttPublisherService.sendCommand(plaqueta.patente, "confirmacion_activacion")
        }
    }

    private fun procesarLectura(payload: String) {
        try {
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
                temperaturaAgua = dto.temperaturaAgua
            )

            lecturaRepository.save(lectura)
        } catch (ex: Exception) {
            logger.error("Error parseando lectura: $payload", ex)
        }
    }

    private fun procesarEstado(payload: String) {
        try {
            val dto = mapper.readValue(payload, PiscinaEstadoDto::class.java)
            val piscina = piscinaRepository.findByPlaqueta(
                plaquetaRepository.findByPatenteAndEstado(dto.patente, EstadoType.ACTIVO)!!
            )

            val estado = EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = dto.entradaAguaActiva.toMutableList(),
                funcionFiltroActivo = dto.funcionFiltroActivo
            )

            estadoPiscinaRepository.save(estado)
        } catch (ex: Exception) {
            logger.error("Error parseando estado: $payload", ex)
        }
    }

    private fun extractPatente(payload: String): String =
        Regex("\"patente\":\\s*\"(\\w{4})\"")
            .find(payload)?.groups?.get(1)?.value ?: "UNKNOWN"
}
