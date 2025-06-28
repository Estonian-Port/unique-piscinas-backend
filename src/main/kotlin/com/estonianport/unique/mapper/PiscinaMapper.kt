package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.PiscinaEquipamientoResponseDto
import com.estonianport.unique.dto.response.PiscinaListResponseDto
import com.estonianport.unique.dto.response.PiscinaProgramacionResponseDto
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

    fun buildPiscinaResumenDto(piscina: Piscina, ph : Double, diferenciaPh : Double): PiscinaResumenResponseDto {
        return PiscinaResumenResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            ph = ph.toString(),
            diferenciaPh = diferenciaPh.toString(),
            clima = piscina.climaLocal().toString(),
            entradaAgua = piscina.entradaAgua.map { it.toString() }.toList(),
            funcionActiva = piscina.funcionActiva.map { it.toString() }.toList(),
            sistemasGermicidas = piscina.sistemaGermicida.map { it.toString() }.toList(),
            calefaccion = piscina.tieneCalefaccion(),
        )
    }

    fun buildPiscinaEquipamientoResponseDto(piscina: Piscina, presion : Double): PiscinaEquipamientoResponseDto {
        return PiscinaEquipamientoResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            estadoFiltro = piscina.filtroActivo(),
            entradaAgua = piscina.entradaAgua.map { it.toString() }.toList(),
            funcionActiva = piscina.funcionActiva.map { it.toString() }.toList(),
            presion = presion.toString(),
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

    fun buildPiscinaProgramacionResponseDto(piscina: Piscina): PiscinaProgramacionResponseDto {
        return PiscinaProgramacionResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            programacionLuces = piscina.programacionLuces.map { ProgramacionMapper.buildProgramacionResponseDto(it) }.toList(),
            programacionFiltrado = piscina.programacionFiltrado.map { ProgramacionMapper.buildProgramacionResponseDto(it) }.toList()
        )
    }
}