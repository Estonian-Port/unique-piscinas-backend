package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.CalefaccionRequestDto
import com.estonianport.unique.dto.response.CalefaccionResponseDto
import com.estonianport.unique.model.Calefaccion
import com.estonianport.unique.model.enums.CalefaccionType



object CalefaccionMapper {

    fun buildCalefaccionResponseDto(calefaccion: Calefaccion) : CalefaccionResponseDto {
        return CalefaccionResponseDto(
            id = calefaccion.id,
            tipo = calefaccion.tipo.toDisplayString(),
            marca = calefaccion.marca,
            modelo = calefaccion.modelo,
            potencia = calefaccion.potencia,
            activa = calefaccion.activa
        )
    }

    fun buildCalefaccion (calefaccionDTO : CalefaccionRequestDto) : Calefaccion {
        return Calefaccion(
            id = calefaccionDTO.id ?: 0,
            tipo = CalefaccionType.valueOf(calefaccionDTO.tipo),
            potencia = calefaccionDTO.potencia,
            modelo = calefaccionDTO.modelo,
            marca = calefaccionDTO.marca,
            activa = calefaccionDTO.activa
        )
    }
}