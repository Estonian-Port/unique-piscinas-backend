package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.PiscinaRequestDto
import com.estonianport.unique.dto.request.ProgramacionFiltradoRequestDto
import com.estonianport.unique.dto.request.ProgramacionLucesRequestDto
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

    @GetMapping("/getAll/{usuarioId}")
    fun getAllPiscinasByUsuarioId(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = piscinaService.getPiscinasByUsuarioId(usuarioId)
                    .map { PiscinaMapper.buildPiscinaListResponseDto(it) }
            )
        )
    }

    @GetMapping("/header/{piscinaId}")
    fun getPiscinaHeader(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscina para header obtenida correctamente",
                data = PiscinaMapper.buildPiscinaHeaderResponseDto(
                    piscinaService.findById(piscinaId)
                )
            )
        )
    }

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
                    piscinaService.getPresion(piscinaId)
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

    @PostMapping("/alta")
    fun savePiscina(@RequestBody piscinaDto: PiscinaRequestDto): ResponseEntity<CustomResponse> {
        val newPiscina = PiscinaMapper.buildPiscina(piscinaDto)
        if (piscinaDto.administradorId != null) {
            newPiscina.administrador = usuarioService.findById(piscinaDto.administradorId).apply {
                if (this == null) {
                    return ResponseEntity.status(400).body(
                        CustomResponse(
                            message = "No se encontró el administrador con id ${piscinaDto.administradorId}",
                            data = null
                        )
                    )
                }
                this.piscinaAsignada()
            }
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

    @PostMapping("/programacion-luces/{piscinaId}")
    fun createProgramacionPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO : ProgramacionLucesRequestDto): ResponseEntity<CustomResponse> {
        val nuevaProgramacion = ProgramacionMapper.buildProgramacionIluminacion(programacionDTO)
        piscinaService.agregarProgramacionLuces(piscinaId, nuevaProgramacion)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación luces de la piscina creada correctamente",
                data = ProgramacionMapper.buildProgramacionIluminacionResponseDto(nuevaProgramacion)
            )
        )
    }

    @PostMapping("/programacion-filtrado/{piscinaId}")
    fun createProgramacionPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO : ProgramacionFiltradoRequestDto): ResponseEntity<CustomResponse> {
        val nuevaProgramacion = ProgramacionMapper.buildProgramacionFiltrado(programacionDTO)
        piscinaService.agregarProgramacionFiltrado(piscinaId, nuevaProgramacion)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación filtrado de la piscina creada correctamente",
                data = ProgramacionMapper.buildProgramacionFiltradoResponseDto(nuevaProgramacion)
            )
        )
    }

    @PutMapping("/programacion-luces-update/{piscinaId}")
    fun updateProgramacionLucesPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO: ProgramacionLucesRequestDto): ResponseEntity<CustomResponse> {
        val programacionActualizada = ProgramacionMapper.buildProgramacionIluminacion(programacionDTO)
        piscinaService.updateProgramacionLuces(piscinaId, programacionActualizada)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación luces de la piscina actualizada correctamente",
                data = ProgramacionMapper.buildProgramacionIluminacionResponseDto(programacionActualizada)
            )
        )
    }

    @PutMapping("/programacion-filtrado-update/{piscinaId}")
    fun updateProgramacionLucesPiscina(@PathVariable piscinaId: Long, @RequestBody programacionDTO: ProgramacionFiltradoRequestDto): ResponseEntity<CustomResponse> {
        val programacionActualizada = ProgramacionMapper.buildProgramacionFiltrado(programacionDTO)
        piscinaService.updateProgramacionFiltrado(piscinaId, programacionActualizada)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación filtrado de la piscina actualizada correctamente",
                data = ProgramacionMapper.buildProgramacionFiltradoResponseDto(programacionActualizada)
            )
        )
    }

}