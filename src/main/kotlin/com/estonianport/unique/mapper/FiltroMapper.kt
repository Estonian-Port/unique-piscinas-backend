package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.FiltroRequestDto
import com.estonianport.unique.dto.response.FiltroResponseDto
import com.estonianport.unique.model.*

object FiltroMapper {

    fun buildFiltroResponseDto(filtro: Filtro): FiltroResponseDto {
        return FiltroResponseDto(
            id = filtro.id,
            tipo = when (filtro) {
                is FiltroArena -> "Arena"
                is FiltroVidrio -> "Vidrio"
                is FiltroCartucho -> "Cartucho"
                else -> "DESCONOCIDO"
            },
            marca = filtro.marca,
            modelo = filtro.modelo,
            diametro = filtro.diametro,
            activo = filtro.activo,
            datoExtra = when (filtro) {
                is FiltroArena -> filtro.cantidadArena.toDouble()
                is FiltroVidrio -> filtro.cantidadVidrio.toDouble()
                is FiltroCartucho -> filtro.micrasDelCartucho.toDouble()
                else -> 0.0
            }
        )
    }

    fun buildFiltro(filtroDTO: FiltroRequestDto): Filtro {
        return when (filtroDTO.tipo) {
            "Arena" -> FiltroArena(
                id = filtroDTO.id ?: 0,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                cantidadArena = filtroDTO.datoExtra,
                //tiempoDeVidaUtil = filtroDTO.tiempoDeVidaUtil
            )
            "Vidrio" -> FiltroVidrio(
                id = filtroDTO.id ?: 0,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                cantidadVidrio = filtroDTO.datoExtra,
                //tiempoDeVidaUtil = filtroDTO.tiempoDeVidaUtil
            )
            "Cartucho" -> FiltroCartucho(
                id = filtroDTO.id ?: 0,
                marca = filtroDTO.marca,
                modelo = filtroDTO.modelo,
                diametro = filtroDTO.diametro,
                activo = filtroDTO.activo,
                micrasDelCartucho = filtroDTO.datoExtra
                //tiempoDeVidaUtil = filtroDTO.tiempoDeVidaUtil
            )
            else -> throw IllegalArgumentException("Tipo de filtro no reconocido")
        }
    }
}