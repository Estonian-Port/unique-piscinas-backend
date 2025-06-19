package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.PiscinaEquipamientoResponseDto
import com.estonianport.unique.dto.response.PiscinaListResponseDto
import com.estonianport.unique.dto.response.PiscinaResumenResponseDto
import com.estonianport.unique.model.Piscina

object PiscinaMapper {

    fun buildPiscinaListDto(piscina: Piscina): PiscinaListResponseDto {
        return PiscinaListResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion
        )
    }

    fun buildPiscinaResumenDto(piscina: Piscina): PiscinaResumenResponseDto {
        return PiscinaResumenResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            ph = piscina.ph().toString(),
            diferenciaPh = piscina.diferenciaPh().toString(),
            clima = piscina.climaLocal().toString(),
            entradaAgua = piscina.entradaAgua.map { it.toString() }.toList(),
            funcionActiva = piscina.funcionActiva.map { it.toString() }.toList(),
            sistemasGermicidas = piscina.sistemaGermicida.map { it.toString() }.toList(),
            calefaccion = piscina.tieneCalefaccion()
        )
    }

    fun buildPiscinaEquipamientoResponseDto(piscina: Piscina): PiscinaEquipamientoResponseDto {
        return PiscinaEquipamientoResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            estadoFiltro = piscina.filtroActivo(),
            entradaAgua = piscina.entradaAgua.map { it.toString() }.toList(),
            funcionActiva = piscina.funcionActiva.map { it.toString() }.toList(),
            presion = piscina.presion().toString(),
            bombas = piscina.bomba.map { BombaMapper.buildBombaResponseDto(it) }.toList(),
            filtro = FiltroMapper.buildFiltroResponseDto(piscina.filtro),
            valvulas = piscina.valvulas.map { ValvulaMapper.buildValvulaResponseDto(it) }.toList(),
            sistemasGermicidas = piscina.sistemaGermicida.map {
                SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(
                    it
                )
            }.toList(),
        )
    }
}