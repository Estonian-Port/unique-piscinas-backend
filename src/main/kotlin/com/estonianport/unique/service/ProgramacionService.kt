package com.estonianport.unique.service

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.quartz.ProgramacionQuartzService
import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.mapper.ProgramacionMapper
import com.estonianport.unique.model.Programacion
import com.estonianport.unique.repository.PiscinaRepository
import com.estonianport.unique.repository.ProgramacionRepository
import org.springframework.stereotype.Service

@Service
class ProgramacionService(
    private val programacionRepository: ProgramacionRepository,
    private val piscinaRepository: PiscinaRepository,
    private val piscinaService: PiscinaService,
    private val quartzService: ProgramacionQuartzService
) {

    fun create(piscinaId: Long, dto: ProgramacionRequestDto): Programacion {
        val piscina = piscinaService.findById(piscinaId)
        val nuevaProgramacion = ProgramacionMapper.buildProgramacion(dto)
        val saved = programacionRepository.save(nuevaProgramacion)
        quartzService.programar(saved, piscinaId)
        piscina.agregarProgramacion(nuevaProgramacion)
        piscinaRepository.save(piscina)
        return saved
    }

    fun update(id: Long, dto: ProgramacionRequestDto, piscinaId: Long): Programacion {
        val existing = programacionRepository.findById(id).orElseThrow { NotFoundException("Programacion no encontrada") }
        val piscina = piscinaService.findById(piscinaId)
        val programacionActualizada = ProgramacionMapper.buildProgramacion(dto)
        existing.apply {
            horaInicio = programacionActualizada.horaInicio
            horaFin = programacionActualizada.horaFin
            dias = programacionActualizada.dias
            activa = programacionActualizada.activa
            tipo = programacionActualizada.tipo
        }
        val saved = programacionRepository.save(existing)
        // Reprogramar (eliminamos y creamos si está activa)
        quartzService.eliminar(saved.id!!)
        if (saved.activa) quartzService.programar(saved, piscinaId)
        // Actualizar la piscina
        piscinaRepository.save(piscina)
        return saved
    }

    fun delete(id: Long, piscinaId: Long) {
        val piscina = piscinaService.findById(piscinaId)
        val existing = programacionRepository.findById(id).orElseThrow { NotFoundException("Programacion no encontrada") }
        programacionRepository.deleteById(id)
        quartzService.eliminar(id)
        piscina.eliminarProgramacion(existing)
        piscinaRepository.save(piscina)
    }
}
