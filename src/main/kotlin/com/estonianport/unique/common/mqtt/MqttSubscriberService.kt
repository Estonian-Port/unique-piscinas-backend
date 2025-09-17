package com.estonianport.unique.common.mqtt

import com.estonianport.unique.model.enums.UsuarioType
import com.estonianport.unique.repository.PlaquetaRepository
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service

@Service
class MqttSubscriberService(private val mqttClient: MqttClient, private val plaquetaRepository: PlaquetaRepository) {


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
                }
            }
            topic.matches(Regex("plaquetas/.+/respuesta")) -> {
                // Guardar resultados en DB
                saveRespuesta(payload)
                println("Respuesta procesada y guardada")
            }
        }
    }


    private fun extractPatente(payload: String): String {
        // Implementar parseo JSON simple para obtener la patente
        return Regex("\"patente\":\\s*\"(\\w{4})\"").find(payload)?.groups?.get(1)?.value ?: "UNKNOWN"
    }


    private fun saveRespuesta(payload: String) {
        // Implementar parseo JSON y guardar datos en tabla muestras
        // Ejemplo: extraer id_solicitud, valor y timestamp
    }

    init {
        // Suscribirse a registro y respuestas de todas las plaquetas
        mqttClient.subscribe("plaquetas/registro", mqttListener)
        mqttClient.subscribe("plaquetas/+/respuesta", mqttListener)
        println("Suscrito a topics de plaquetas")
    }
}