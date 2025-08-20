package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.ProgramacionFiltradoRequestDto
import com.estonianport.unique.dto.request.ProgramacionLucesRequestDto
import com.estonianport.unique.dto.response.ProgramacionFiltradoResponseDto
import com.estonianport.unique.dto.response.ProgramacionLucesResponseDto
import com.estonianport.unique.model.ProgramacionFiltrado
import com.estonianport.unique.model.ProgramacionLuces
import java.time.DayOfWeek
import java.time.LocalTime

object ProgramacionMapper {

    fun buildProgramacionLucesResponseDto(
        programacion: ProgramacionLuces
    ): ProgramacionLucesResponseDto {
        return ProgramacionLucesResponseDto(
            id = programacion.id.toString(),
            horaInicio = programacion.horaInicio.toString(),
            horaFin = programacion.horaFin.toString(),
            dias = programacion.dias.map { it.toString() },
            activa = programacion.activa,
        )
    }

    fun buildProgramacionFiltradoResponseDto(
        programacion: ProgramacionFiltrado
    ): ProgramacionFiltradoResponseDto {
        return ProgramacionFiltradoResponseDto(
            id = programacion.id.toString(),
            horaInicio = programacion.horaInicio.toString(),
            horaFin = programacion.horaFin.toString(),
            dias = programacion.dias.map { it.toString() },
            activa = programacion.activa,
            funcionFiltro = programacion.funcionFiltro.toString()
        )
    }

    fun buildProgramacionLuces(programacionRequestDto: ProgramacionLucesRequestDto): ProgramacionLuces {
        return ProgramacionLuces(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
        )
    }

    fun buildProgramacionFiltrado(programacionRequestDto: ProgramacionFiltradoRequestDto): ProgramacionFiltrado {
        return ProgramacionFiltrado(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
            funcionFiltro = programacionRequestDto.funcionFiltro
        )
    }

}