package com.estonianport.unique.controller

import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/piscina")
@CrossOrigin("*")
class PiscinaController {
    @Autowired
    lateinit var usuarioService: UsuarioService

    @Autowired
    lateinit var piscinaService: PiscinaService

    // Endpoint para retornar la lista de piscinas asignadas a un usuario
    @GetMapping("/{usuarioId}")
    fun getPiscinasByUsuarioId(@PathVariable usuarioId: String): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = piscinaService.getPiscinasByUsuarioId(usuarioId).map { PiscinaMapper.buildPiscinaListDto(it) }
            )
        )
    }

    // Endpoint para retornar los datos de la seccion resumen de la piscina
    @GetMapping("/resumen/{piscinaId}")
    fun getDataResumenPiscina(@PathVariable piscinaId: String): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Informacion de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaResumenDto(piscinaService.findById(piscinaId))
            )
        )
    }

    // Endpoint para retornar los datos de la seccion resumen de la piscina
    @GetMapping("/resumen/{piscinaId}")
    fun getDataEquipamientoPiscina(@PathVariable piscinaId: String): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Informacion de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaEquipamientoResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

}