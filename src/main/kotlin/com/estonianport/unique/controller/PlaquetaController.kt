package com.estonianport.unique.controller

import com.estonianport.unique.common.mqtt.MqttPublisherService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/plaquetas")
class PlaquetaController(private val mqttPublisherService: MqttPublisherService) {

    @PostMapping("/{patente}/tomar-muestra")
    fun tomarMuestra(@PathVariable patente: String): ResponseEntity<String> {
        val idSolicitud = System.currentTimeMillis().toInt() // solo un ejemplo de ID
        mqttPublisherService.sendCommand(patente, "tomar_muestra", idSolicitud)
        return ResponseEntity.ok("Comando enviado a plaqueta $patente con id $idSolicitud")
    }

}