package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.SistemaGermicidaRequestDto
import com.estonianport.unique.dto.response.SistemaGermicidaResponseDto
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.estonianport.unique.model.enums.toCapitalized

object SistemaGermicidaMapper {

    fun buildSistemaGermicidaResponseDto(germicida : SistemaGermicida): SistemaGermicidaResponseDto {
        return SistemaGermicidaResponseDto(
            id = germicida.id.toString(),
            tipo = germicida.tipo.toCapitalized(),
            vidaRestante = germicida.vidaRestante().toString(),
            estado = germicida.estado()
        )
    }

    fun buildSistemaGermicida (germicida: SistemaGermicidaRequestDto) : SistemaGermicida {
        return SistemaGermicida(
            id = germicida.id,
            tipo = SistemaGermicidaType.valueOf(germicida.tipo),
            estado = EstadoType.OPERATIVO,
        )
    }
}