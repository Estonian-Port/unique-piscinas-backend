package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.PiscinaRequestDto
import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.mapper.ProgramacionMapper
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/piscina")
@CrossOrigin("*")
class PiscinaController {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    lateinit var piscinaService: PiscinaService

    // Te cambie los id que llegan por endpoint a Long, prefiero q se trabajen directo asi q estar casteandolos en el service
    @GetMapping("/{usuarioId}")
    fun getPiscinasByUsuarioId(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = piscinaService.getPiscinasByUsuarioId(usuarioId)
                    .map { PiscinaMapper.buildPiscinaListResponseDto(it) }
            )
        )
    }

    // Endpoint para retornar los datos de la seccion resumen de la piscina
    @GetMapping("/resumen/{piscinaId}")
    fun getDataResumenPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Resumen de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaService.findById(piscinaId)
                )
            )
        )
    }

    @GetMapping("/resumenPh/{piscinaId}")
    fun getDataResumenPiscinaPh(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Resumen de PH de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaPhResponseDto(
                    piscinaService.getPh(piscinaId),
                    piscinaService.getDiferenciaPh(piscinaId)
                )
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
                    //piscinaService.getPresion(piscinaId)
                    2.00
                )
            )
        )
    }

    @GetMapping("lectura/{piscinaId}")
    fun getLecturasPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Lecturas de la piscina obtenida correctamente",
                data = piscinaService.getLecturasPiscina(piscinaId)
            )
        )
    }

    @GetMapping("programacion/{piscinaId}")
    fun getProgramacionPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programaciones de la piscina obtenidas correctamente",
                data = PiscinaMapper.buildPiscinaProgramacionResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @PostMapping("")
    fun createPiscina(@RequestBody piscinaDto: PiscinaRequestDto): ResponseEntity<CustomResponse> {
        val newPiscina = PiscinaMapper.buildPiscina(piscinaDto)
        if (piscinaDto.administradorId != null) {
            newPiscina.administrador = usuarioService.findById(piscinaDto.administradorId)
        }
        
        piscinaService.create(newPiscina)
        return ResponseEntity.status(201).body(
            CustomResponse(
                message = "Piscina creada correctamente",
                data = PiscinaMapper.buildPiscinaResponseDto(newPiscina)
            )
        )
    }
 
    @DeleteMapping("programacion/{piscinaId}/{programacionId}")
    fun getProgramacionPiscina(@PathVariable piscinaId: Long, @PathVariable programacionId: Long): ResponseEntity<CustomResponse> {
        piscinaService.deleteProgramacion(piscinaId, programacionId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina eliminada correctamente",
                data = null
            )
        )
    }

    @PostMapping("/programacion/{piscinaId}")
    fun createProgramacionPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO : ProgramacionRequestDto): ResponseEntity<CustomResponse> {
        // El id de la piscina se puede pasar por path o en el mismo DTO de la programacion podria venir ese dato?
        val nuevaProgramacion = ProgramacionMapper.buildProgramacion(programacionDTO)
        piscinaService.agregarProgramacion(piscinaId, nuevaProgramacion, programacionDTO.filtrado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina creada correctamente",
                data = ProgramacionMapper.buildProgramacionResponseDto(nuevaProgramacion)
            )
        )
    }

    @PutMapping("/programacion/{piscinaId}")
    fun updateProgramacionPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO: ProgramacionRequestDto): ResponseEntity<CustomResponse> {
        val programacionActualizada = ProgramacionMapper.buildProgramacion(programacionDTO)
        piscinaService.updateProgramacion(piscinaId, programacionActualizada, programacionDTO.filtrado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina actualizada correctamente",
                data = ProgramacionMapper.buildProgramacionResponseDto(programacionActualizada)
            )
        )
    }

}