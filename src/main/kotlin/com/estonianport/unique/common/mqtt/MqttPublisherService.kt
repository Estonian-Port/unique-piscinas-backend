package com.estonianport.unique.common.mqtt

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class MqttPublisherService(private val mqttClient: Mqtt5AsyncClient) {

    fun sendCommand(patente: String, accion: String) {
        val topic = "plaquetas/comando"
        val idSolicitud = System.currentTimeMillis()
        val payload =
            """{
                "patente": "$patente",
                "id_solicitud": $idSolicitud,
                "accion": "$accion"
            }"""

        // Use the HiveMQ fluent builder API for publishing
        mqttClient.publishWith()
            .topic(topic)
            .payload(payload.toByteArray(StandardCharsets.UTF_8))
            .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE) // QOS 1
            .send()
            .whenComplete { publishResult, throwable ->
                if (throwable != null) {
                    println("Failed to publish command $accion for plaqueta $patente: ${throwable.message}")
                } else {
                    println("Publicado comando $accion para plaqueta $patente en $topic")
                }
            }
    }

    fun sendCommandList(
        patente: String,
        entradasAgua: List<EntradaAguaType> = emptyList(),
        funcionFiltro: FuncionFiltroType? = null,
        sistemasGermicida: List<SistemaGermicidaType> = emptyList()
    ) {
        val topic = "plaquetas/control"
        val idSolicitud = System.currentTimeMillis()

        val payloadMap = mutableMapOf(
            "patente" to patente,
            "id_solicitud" to idSolicitud,
            "entrada_agua" to entradasAgua,
            "funcion_filtro" to (funcionFiltro ?: ""),
            "sistema_germicida" to sistemasGermicida
        )

        val payloadJson = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(payloadMap)

        // Use the HiveMQ fluent builder API for publishing
        mqttClient.publishWith()
            .topic(topic)
            .payload(payloadJson.toByteArray(StandardCharsets.UTF_8))
            .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE) // QOS 1
            .send()
            .whenComplete { publishResult, throwable ->
                if (throwable != null) {
                    println("Failed to publish command list for plaqueta $patente: ${throwable.message}")
                } else {
                    println("Publicado comando lista para plaqueta $patente en $topic: $payloadJson")
                }
            }
    }
}
