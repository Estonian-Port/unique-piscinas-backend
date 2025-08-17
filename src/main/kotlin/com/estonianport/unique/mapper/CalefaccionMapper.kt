package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.CalefaccionRequestDto
import com.estonianport.unique.dto.response.CalefaccionResponseDto
import com.estonianport.unique.model.Calefaccion
import com.estonianport.unique.model.enums.CalefaccionType

object CalefaccionMapper {

    fun buildCalefaccionResponseDto(calefaccion: Calefaccion) : CalefaccionResponseDto {
        return CalefaccionResponseDto(
            id = calefaccion.id,
            tipo = calefaccion.tipo.toString(),
            estado = calefaccion.activa
        )
    }

    fun buildCalefaccion (calefaccionDTO : CalefaccionRequestDto) : Calefaccion {
        return Calefaccion(
            id = calefaccionDTO.id,
            tipo = CalefaccionType.valueOf(calefaccionDTO.tipo),
            potencia = calefaccionDTO.potencia,
            modelo = calefaccionDTO.modelo,
            marca = calefaccionDTO.marca,
            estado = calefaccionDTO.estado,
            activa = calefaccionDTO.activa
        )
    }
}