package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.FiltroResponseDto
import com.estonianport.unique.model.Filtro

object FiltroMapper {

    fun buildFiltroResponseDto ( filtro : Filtro) : FiltroResponseDto {
        return FiltroResponseDto(
            marca = filtro.marca,
            modelo = filtro.modelo,
            diametro = filtro.diametro
        )
    }
}