package com.estonianport.unique.controller

import com.estonianport.unique.common.mqtt.MqttPublisherService
import com.estonianport.unique.dto.request.ComandoRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/plaquetas")
class PlaquetaController(private val mqttPublisherService: MqttPublisherService) {

    @PostMapping("/{patente}/comando")
    fun tomarMuestra(@PathVariable patente: String, @RequestBody comandoRequestDto: ComandoRequestDto): ResponseEntity<String> {
        mqttPublisherService.sendCommand(patente, comandoRequestDto.accion)
        return ResponseEntity.ok("Comando enviado a plaqueta $patente")
    }

}