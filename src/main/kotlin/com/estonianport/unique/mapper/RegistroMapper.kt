package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.RegistroResponseDto
import com.estonianport.unique.model.Registro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RegistroMapper {

    fun buildRegistroResponseDto(registro: Registro) : RegistroResponseDto {
        return RegistroResponseDto (
            id = registro.id,
            fecha = registro.fecha.toString(),
            dispositivo = registro.dispositivo,
            accion = registro.accion,
            descripcion = registro.descripcion,
            nombreTecnico = registro.nombreTecnico,
        )
    }

    fun buildRegistro ( registroDto: RegistroResponseDto ) : Registro {
        return Registro (
            id = registroDto.id,
            fecha = LocalDate.parse(registroDto.fecha, DateTimeFormatter.ISO_DATE),
            dispositivo = registroDto.dispositivo,
            accion = registroDto.accion,
            descripcion = registroDto.descripcion,
            nombreTecnico = registroDto.nombreTecnico,
        )
    }

}