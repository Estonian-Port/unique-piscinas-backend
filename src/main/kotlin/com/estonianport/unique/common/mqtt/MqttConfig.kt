package com.estonianport.unique.common.mqtt

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
class MqttConfig(private val mqttProperties: MqttProperties) {

    @Bean
    fun mqttClient(): Mqtt5AsyncClient {
        val brokerUrl = mqttProperties.brokerUrl
        val uri = URI(brokerUrl)
        val host = uri.host

        // Si el esquema es WSS, usamos 443 por defecto, si no, el puerto por defecto de URI o 1883
        val port = if (uri.port == -1) (if (uri.scheme == "wss") 443 else 1883) else uri.port
        val path = if (uri.path.isNullOrBlank()) "/mqtt" else uri.path

        // Iniciamos el builder
        val builder = MqttClient.builder()
            .serverHost(host)
            .serverPort(port)
            .useMqttVersion5() // Usamos MQTT 5

        // Configuramos WebSocket y SSL si corresponde
        if (uri.scheme == "wss" || uri.scheme == "ws") {
            builder.webSocketConfig()
                .serverPath(path)
                .subprotocol("mqtt")
                .applyWebSocketConfig()
        }

        if (uri.scheme == "wss" || uri.scheme == "mqtts") {
            // Aplicamos configuración SSL por defecto (necesario para 443 WSS)
            builder.sslWithDefaultConfig()
        }

        val client = builder.buildAsync()

        // Iniciamos la conexión asíncrona
        client.connectWith()
            .simpleAuth()
            .username(mqttProperties.username)
            .password(mqttProperties.password.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck, throwable ->
                if (throwable != null) {
                    println("MQTT Connection failed to $brokerUrl: ${throwable.message}")
                } else {
                    println("MQTT Client connected successfully to $brokerUrl!")
                }
            }

        return client
    }
}