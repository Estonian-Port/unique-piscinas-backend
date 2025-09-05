package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.ValvulaResponseDto
import com.estonianport.unique.model.Valvula
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.model.enums.ValvulaType
import com.estonianport.unique.model.enums.toCapitalized

object ValvulaMapper {

    fun buildValvulaResponseDto(valvula: Valvula): ValvulaResponseDto {
        return ValvulaResponseDto(
            id = valvula.id,
            tipo = valvula.tipo.toCapitalized(),
            estado = valvula.estado()
        )
    }
}