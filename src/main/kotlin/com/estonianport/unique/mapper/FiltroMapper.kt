package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.FiltroRequestDto
import com.estonianport.unique.dto.response.FiltroResponseDto
import com.estonianport.unique.model.*

object FiltroMapper {

    fun buildFiltroResponseDto(filtro: Filtro): FiltroResponseDto {
        return FiltroResponseDto(
            marca = filtro.marca,
            modelo = filtro.modelo,
            diametro = filtro.diametro,
            activo = filtro.activo
        )
    }

    fun buildFiltro(filtroDTO: FiltroRequestDto): Filtro {
        return when {
            filtroDTO.cantidadArena != null -> FiltroArena(
                id = filtroDTO.id,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                cantidadArena = filtroDTO.cantidadArena
            )

            filtroDTO.cantidadVidrio != null -> FiltroVidrio(
                id = filtroDTO.id,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                cantidadVidrio = filtroDTO.cantidadVidrio
            )

            filtroDTO.micrasDelCartucho != null -> FiltroCartucho(
                id = filtroDTO.id,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                micrasDelCartucho = filtroDTO.micrasDelCartucho
            )

            else -> FiltroDiatomea(
                id = filtroDTO.id,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
            )
        }
    }
}