package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.ValvulaResponseDto
import com.estonianport.unique.model.Valvula

object ValvulaMapper {

    fun buildValvulaResponseDto(valvula: Valvula): ValvulaResponseDto {
        return ValvulaResponseDto(
            id = valvula.id,
            tipo = valvula.tipo.toString(),
            estado = valvula.estado()
        )
    }
}