/*package com.estonianport.unique.common.mqtt

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service

@Service
class MqttPublisherService(private val mqttClient: MqttClient) {

    fun sendCommand(patente: String, accion: String) {
        val topic = "plaquetas/comando"
        val idSolicitud = System.currentTimeMillis()
        val payload =
            """{
                "patente": "$patente",
                "id_solicitud": $idSolicitud,
                "accion": "$accion"
            }"""
        val message = MqttMessage(payload.toByteArray()).apply { qos = 1 }
        //mqttClient.publish(topic, message)
        println("Publicado comando $accion para plaqueta $patente en $topic")
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
        val message = MqttMessage(payloadJson.toByteArray()).apply { qos = 1 }
        //mqttClient.publish(topic, message)
        println("Publicado comando lista para plaqueta $patente en $topic: $payloadJson")
    }
}*/

package com.estonianport.unique.common.mqtt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import jakarta.annotation.PostConstruct
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MqttPublisherService(
    private val mqttClient: MqttClient,
    private val mqttConnectOptions: MqttConnectOptions
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        connectIfNeeded()
    }

    private fun connectIfNeeded() {
        try {
            if (!mqttClient.isConnected) {
                mqttClient.connect(mqttConnectOptions)
                logger.info("✅ Conectado al broker MQTT")
            }
        } catch (ex: Exception) {
            logger.warn("⚠️ No se pudo conectar a MQTT: ${ex.message}")
        }
    }

    fun sendCommand(patente: String, accion: String) {
        val topic = "plaquetas/comando"
        val payload = mapOf(
            "patente" to patente,
            "id_solicitud" to System.currentTimeMillis(),
            "accion" to accion
        )

        publish(topic, payload)
    }

    fun sendCommandList(
        patente: String,
        entradasAgua: List<EntradaAguaType> = emptyList(),
        funcionFiltro: FuncionFiltroType? = null,
        sistemasGermicida: List<SistemaGermicidaType> = emptyList()
    ) {
        val topic = "plaquetas/control"
        val payload = mapOf(
            "patente" to patente,
            "id_solicitud" to System.currentTimeMillis(),
            "entrada_agua" to entradasAgua,
            "funcion_filtro" to funcionFiltro,
            "sistema_germicida" to sistemasGermicida
        )

        publish(topic, payload)
    }

    private fun publish(topic: String, payload: Any) {
        try {
            connectIfNeeded()

            if (!mqttClient.isConnected) {
                logger.warn("❌ No conectado a MQTT. Mensaje no enviado a $topic")
                return
            }

            val json = objectMapper.writeValueAsString(payload)
            val message = MqttMessage(json.toByteArray()).apply { qos = 1 }

            mqttClient.publish(topic, message)
            logger.info("📤 Publicado en $topic: $json")

        } catch (ex: MqttException) {
            logger.error("🔥 Error publicando en MQTT ($topic)", ex)
        }
    }
}
