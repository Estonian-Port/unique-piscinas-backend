package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.service.ProgramacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/programaciones")
class ProgramacionController {

    @Autowired
    private lateinit var programacionService: ProgramacionService

    @PostMapping("/{piscinaId}")
    fun create(@PathVariable piscinaId: Long, @RequestBody dto: ProgramacionRequestDto): ResponseEntity<Any> {
        val saved = programacionService.create(piscinaId, dto)
        return ResponseEntity.ok(saved)
    }

    @PutMapping("/{id}/{piscinaId}")
    fun update(@PathVariable id: Long, @PathVariable piscinaId: Long, @RequestBody dto: ProgramacionRequestDto): ResponseEntity<Any> {
        val updated = programacionService.update(id, dto, piscinaId)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}/{idPiscina}")
    fun delete(@PathVariable id: Long, @PathVariable idPiscina: Long): ResponseEntity<Any> {
        programacionService.delete(id, idPiscina)
        return ResponseEntity.ok().build()
    }

}
