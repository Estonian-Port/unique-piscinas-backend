package com.estonianport.unique.common.mqtt

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service

@Service
class MqttPublisherService(private val mqttClient: MqttClient) {

    fun sendCommand(patente: String, accion: String) {
        val topic = "plaquetas/$patente/comando"
        val idSolicitud = System.currentTimeMillis().toInt()
        val payload =
            """{
                "accion": "$accion",
                "id_solicitud": $idSolicitud
            }"""
        val message = MqttMessage(payload.toByteArray()).apply { qos = 1 }
        mqttClient.publish(topic, message)
        println("Publicado comando $accion en $topic")
    }

    fun sendCommandList(
        patente: String,
        entradasAgua: List<EntradaAguaType> = emptyList(),
        funcionFiltro: FuncionFiltroType? = null,
        sistemasGermicida: List<SistemaGermicidaType> = emptyList()
    ) {
        val topic = "plaquetas/$patente/comandos"

        val payloadMap = mutableMapOf(
            "entrada_agua" to (entradasAgua),
            "funcion_filtro" to (funcionFiltro ?: ""),
            "sistema_germicida" to (sistemasGermicida)
        )

        val payloadJson = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(payloadMap)
        val message = MqttMessage(payloadJson.toByteArray()).apply { qos = 1 }
        mqttClient.publish(topic, message)
        println("Publicado comando lista en $topic: $payloadJson")
    }
}
