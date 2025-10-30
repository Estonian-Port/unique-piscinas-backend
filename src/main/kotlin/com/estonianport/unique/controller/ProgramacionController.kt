package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.mapper.ProgramacionMapper
import com.estonianport.unique.service.EstadoPiscinaService
import com.estonianport.unique.service.ProgramacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/programaciones-piscina")
@CrossOrigin("*")
class ProgramacionController {

    @Autowired
    lateinit var programacionService: ProgramacionService

    @Autowired
    lateinit var estadoPiscinaService: EstadoPiscinaService

    @PostMapping("/add-programacion/{piscinaId}")
    fun createProgramacionPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevaProgramacion = ProgramacionMapper.buildProgramacion(programacionDTO)
        programacionService.agregarProgramacion(piscinaId, nuevaProgramacion, estadoPiscinaService)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación filtrado de la piscina creada correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-programacion/{piscinaId}/{programacionId}")
    fun deleteProgramacionFiltrado(
        @PathVariable piscinaId: Long,
        @PathVariable programacionId: Long,
    ): ResponseEntity<CustomResponse> {
        programacionService.deleteProgramacion(piscinaId, programacionId, estadoPiscinaService)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación eliminada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/desactivar-programacion/{piscinaId}/{programacionId}")
    fun desactivarProgramacion(
        @PathVariable piscinaId: Long,
        @PathVariable programacionId: Long
    ): ResponseEntity<CustomResponse> {
        programacionService.desactivarProgramacion(piscinaId, programacionId, estadoPiscinaService)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "🛑 Programación desactivada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/activar-programacion/{piscinaId}/{programacionId}")
    fun activarProgramacion(
        @PathVariable piscinaId: Long,
        @PathVariable programacionId: Long
    ): ResponseEntity<CustomResponse> {
        programacionService.activarProgramacion(piscinaId, programacionId, estadoPiscinaService)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "✅ Programación activada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-programacion/{piscinaId}")
    fun updateProgramacion(
        @PathVariable piscinaId: Long,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        val programacionActualizada = ProgramacionMapper.buildProgramacion(programacionDTO)
        programacionService.updateProgramacion(piscinaId, programacionActualizada, estadoPiscinaService)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina actualizada correctamente",
                data = null
            )
        )
    }

}