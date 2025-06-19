package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.SistemaGermicidaResponseDto
import com.estonianport.unique.model.SistemaGermicida

object SistemaGermicidaMapper {

    fun buildSistemaGermicidaResponseDto(germicida : SistemaGermicida): SistemaGermicidaResponseDto {
        return SistemaGermicidaResponseDto(
            id = germicida.id.toString(),
            tipo = germicida.tipo.toString(),
            vidaRestante = germicida.vidaRestante().toString(),
            estado = germicida.estado()
        )
    }
}