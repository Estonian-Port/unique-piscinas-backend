package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.RegistroResponseDto
import com.estonianport.unique.model.Registro

object RegistroMapper {

    fun buildRegistroResponseDto(registro: Registro) : RegistroResponseDto {
        return RegistroResponseDto (
            id = registro.id,
            fecha = registro.fecha.toString(),
            dispositivo = registro.dispositivo,
            accion = registro.accion,
            descripcion = registro.descripcion,
            nombreTecnico = registro.tecnico.nombre,
        )
    }

}