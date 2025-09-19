package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.dto.response.ProgramacionResponseDto
import com.estonianport.unique.model.ProgramacionFiltrado
import com.estonianport.unique.model.ProgramacionIluminacion
import com.estonianport.unique.model.enums.FuncionFiltro
import com.estonianport.unique.model.enums.ProgramacionType
import java.time.DayOfWeek
import java.time.LocalTime

object ProgramacionMapper {

    fun buildProgramacionIluminacionResponseDto(
        programacion: ProgramacionIluminacion
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

    fun buildProgramacionFiltradoResponseDto(
        programacion: ProgramacionFiltrado
    ): ProgramacionResponseDto {
        return ProgramacionResponseDto(
            id = programacion.id.toString(),
            horaInicio = programacion.horaInicio.toString(),
            horaFin = programacion.horaFin.toString(),
            dias = programacion.dias.map { it.toString() },
            activa = programacion.activa,
            tipo = ProgramacionType.FILTRADO
        )
    }

    fun buildProgramacionIluminacion(programacionRequestDto: ProgramacionRequestDto): ProgramacionIluminacion {
        return ProgramacionIluminacion(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
        )
    }

    fun buildProgramacionFiltrado(programacionRequestDto: ProgramacionRequestDto): ProgramacionFiltrado {
        return ProgramacionFiltrado(
            id = programacionRequestDto.id,
            horaInicio = LocalTime.parse(programacionRequestDto.horaInicio),
            horaFin = LocalTime.parse(programacionRequestDto.horaFin),
            dias = programacionRequestDto.dias.map { DayOfWeek.valueOf(it.uppercase()) }.toMutableList(),
            activa = programacionRequestDto.activa,
            funcionFiltro = FuncionFiltro.FILTRAR
        )
    }

}