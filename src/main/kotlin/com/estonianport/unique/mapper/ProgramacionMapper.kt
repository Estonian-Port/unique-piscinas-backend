package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.dto.response.ProgramacionResponseDto
import com.estonianport.unique.model.Programacion
import com.estonianport.unique.model.enums.ProgramacionType
import java.time.DayOfWeek
import java.time.LocalTime

object ProgramacionMapper {

    fun buildProgramacionResponseDto(
        programacion: Programacion
    ): ProgramacionResponseDto {
        return ProgramacionResponseDto(
            id = programacion.id.toString(),
            horaInicio = programacion.horaInicio.toString(),
            horaFin = programacion.horaFin.toString(),
            dias = programacion.dias.map { it.toString() },
            activa = programacion.activa,
            tipo = ProgramacionType.ILUMINACION
        )
    }

    fun buildProgramacion(programacionRequestDto: ProgramacionRequestDto): Programacion {
        return Programacion(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
            tipo = ProgramacionType.valueOf(programacionRequestDto.tipo),
        )
    }

}