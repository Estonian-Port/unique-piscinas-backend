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
    lateinit var piscinaService: PiscinaService

    // Te cambie los id que llegan por endpoint a Long, prefiero q se trabajen directo asi q estar casteandolos en el service
    @GetMapping("/{usuarioId}")
    fun getPiscinasByUsuarioId(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = piscinaService.getPiscinasByUsuarioId(usuarioId).map { PiscinaMapper.buildPiscinaListDto(it) }
            )
        )
    }

    // Endpoint para retornar los datos de la seccion resumen de la piscina
    @GetMapping("/resumen/{piscinaId}")
    fun getDataResumenPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Resumen de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaResumenDto(
                    piscinaService.findById(piscinaId),
                    piscinaService.getPh(piscinaId),
                    piscinaService.getDiferenciaPh(piscinaId))
            )
        )
    }

    // Endpoint para retornar el equipamiento de la piscina
    @GetMapping("/equipamiento/{piscinaId}")
    fun getDataEquipamientoPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Equipamiento de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaEquipamientoResponseDto(
                    piscinaService.findById(piscinaId),
                    piscinaService.getPresion(piscinaId))
            )
        )
    }

    @GetMapping("lectura/{piscinaId}")
    fun getLecturasPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Lecturas de la piscina obtenida correctamente",
                // TODO Aca no se si harias un mapper, despues acomodalo como mejor creas yo suelo hacerlo asi
                data = piscinaService.getLecturasPiscina(piscinaId)
            )
        )
    }

}