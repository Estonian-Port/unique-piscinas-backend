package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.ValvulaRequestDto
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

    fun buildValvula(valvulaDTO: ValvulaRequestDto): Valvula {
        return Valvula(
            id = valvulaDTO.id,
            tipo = ValvulaType.valueOf(valvulaDTO.tipo),
            estado = EstadoType.valueOf(valvulaDTO.estado)
        )
    }
}