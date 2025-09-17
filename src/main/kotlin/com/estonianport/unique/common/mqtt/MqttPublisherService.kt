package com.estonianport.unique.common.mqtt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service

@Service
class MqttPublisherService(private val mqttClient: MqttClient) {


    fun sendCommand(patente: String, accion: String, idSolicitud: Int) {
        val topic = "plaquetas/$patente/comando"
        val payload = "{" +
                "\"accion\": \"$accion\", " +
                "\"id_solicitud\": $idSolicitud" +
                "}"
        val message = MqttMessage(payload.toByteArray()).apply { qos = 1 }
        mqttClient.publish(topic, message)
        println("Publicado comando $accion en $topic")
    }


    fun sendActivationConfirmation(patente: String) {
        val topic = "plaquetas/$patente/comando"
        val payload = "{\"accion\": \"confirmacion_activacion\"}"
        val message = MqttMessage(payload.toByteArray()).apply { qos = 1 }
        mqttClient.publish(topic, message)
        println("Confirmación de activación enviada a $topic")
    }
}