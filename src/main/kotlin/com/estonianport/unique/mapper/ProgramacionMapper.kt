package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.ProgramacionResponseDto
import com.estonianport.unique.model.Programacion

object ProgramacionMapper {

    fun buildProgramacionResponseDto(
        programacion: Programacion
    ): ProgramacionResponseDto {
        return ProgramacionResponseDto(
            id = programacion.id.toString(),
            horaInicio = programacion.horaInicio.toString(),
            horaFin = programacion.horaFin.toString(),
            dias = programacion.dias.map { it.toString() },
            estaActivo = programacion.estaActivo
        )
    }

}