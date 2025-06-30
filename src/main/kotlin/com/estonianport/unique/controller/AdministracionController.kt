package com.estonianport.unique.controller

import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.service.AdministracionService
import com.estonianport.unique.service.PiscinaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/administracion")
@CrossOrigin("*")
class AdministracionController {

    @Autowired
    lateinit var administracionService: AdministracionService

    // Tengo la duda si todos los endpoints de administracion deberian chequear si el usuario es administrador
    @GetMapping("/estadisticas")
    fun getEstadisticas(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = administracionService.getEstadisticas()
            )
        )
    }

}