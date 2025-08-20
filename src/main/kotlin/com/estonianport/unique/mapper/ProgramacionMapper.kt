package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.dto.response.ProgramacionResponseDto
import com.estonianport.unique.model.Programacion
import java.time.DayOfWeek
import java.time.LocalDate
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
            funcionFiltro = programacion.funcionFiltro.toString(),
            estaActivo = programacion.activa,
            tipo = programacion.tipo.toString(),
        )
    }

    fun buildProgramacion(programacionRequestDto: ProgramacionRequestDto): Programacion {
        return Programacion(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            funcionFiltro = programacionRequestDto.funcionFiltro,
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
            tipo = programacionRequestDto.tipo,
        )
    }

}