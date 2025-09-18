package com.estonianport.unique.common.mqtt

import com.estonianport.unique.dto.response.LecturaResponseDTO
import com.estonianport.unique.model.Lectura
import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.UsuarioType
import com.estonianport.unique.repository.LecturaRepository
import com.estonianport.unique.repository.PiscinaRepository
import com.estonianport.unique.repository.PlaquetaRepository
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
    private val mqttPublisherService : MqttPublisherService
) {

    private val mqttListener = IMqttMessageListener { topic, message ->
        val payload = String(message.payload)
        println("Mensaje recibido en $topic: $payload")

        when {
            topic == "plaquetas/registro" -> {
                // Procesar registro
                val patente = extractPatente(payload)
                val plaqueta = plaquetaRepository.findByPatenteAndEstado(patente, UsuarioType.PENDIENTE)
                if (plaqueta != null) {
                    plaqueta.estado = UsuarioType.ACTIVO
                    plaquetaRepository.save(plaqueta)
                    println("Plaqueta $patente activada")
                    mqttPublisherService.sendActivationConfirmation(patente)
                }
            }
            topic.matches(Regex("plaquetas/.+/respuesta")) -> {
                // Guardar resultados en DB
                val patente = Regex("plaquetas/(.+)/respuesta")
                    .find(topic)
                    ?.groups?.get(1)?.value

                if (patente != null) {
                    val plaqueta = plaquetaRepository.findByPatenteAndEstado(patente, UsuarioType.ACTIVO)
                    if(plaqueta !=null){
                        saveRespuesta(payload,plaqueta)
                        println("Respuesta de $patente procesada y guardada")
                    }else{
                        println("Patente $patente no encontrada en base de datos o no esta activa")
                    }
                } else {
                    println("No se pudo extraer la patente del topic: $topic")
                }
            }
        }
    }


    private fun extractPatente(payload: String): String {
        // Implementar parseo JSON simple para obtener la patente
        return Regex("\"patente\":\\s*\"(\\w{4})\"").find(payload)?.groups?.get(1)?.value ?: "UNKNOWN"
    }

    private fun saveRespuesta(payload: String, plaqueta : Plaqueta) {
        try {
            val mapper = jacksonObjectMapper()
            val dto = mapper.readValue(payload, LecturaResponseDTO::class.java)

            val lectura = Lectura(
                piscina = piscinaRepository.findByPlaqueta(plaqueta),
                ph = dto.ph,
                cloro = dto.cloro,
                temperatura = dto.temperatura,
                presion = dto.presion
            )

            lecturaRepository.save(lectura)
            println("Lectura guardada correctamente: $lectura")

        } catch (ex: Exception) {
            println("Error parseando payload: $payload")
        }
    }

    init {
        // Suscribirse a registro y respuestas de todas las plaquetas
        mqttClient.subscribe("plaquetas/registro", mqttListener)
        mqttClient.subscribe("plaquetas/+/respuesta", mqttListener)
        println("Suscrito a topics de plaquetas")
    }
}