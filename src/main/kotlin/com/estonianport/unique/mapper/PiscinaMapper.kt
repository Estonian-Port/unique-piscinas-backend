package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.PiscinaRequestDto
import com.estonianport.unique.dto.response.*
import com.estonianport.unique.model.Piscina

object PiscinaMapper {

    fun buildPiscinaListResponseDto(piscina: Piscina): PiscinaListResponseDto {
        return PiscinaListResponseDto(
            id = piscina.id.toString(),
            nombre = piscina.nombre,
            direccion = piscina.direccion
        )
    }

    fun buildPiscinaResumenResponseDto(piscina: Piscina, ph : Double, diferenciaPh : Double): PiscinaResumenResponseDto {
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

    fun buildPiscinaRegistradaListDto(piscina: Piscina, ph: Double?): PiscinaRegistradaListResponseDto {
        return PiscinaRegistradaListResponseDto(
            id = piscina.id,
            nombre = piscina.nombre,
            esDesbordante = piscina.esDesbordante,
            administradorNombre = piscina.administrador?.nombre ?: "Sin administrador asignado",
            ph = ph ?: 0.0,
            sistemasGermicidas = piscina.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(it) },
            calefaccion = piscina.calefaccion?.let { CalefaccionMapper.buildCalefaccionResponseDto(it) },
        )
    }

    fun buildPiscinaAsignadaResponseDto(piscina: Piscina): PiscinaAsignadaResponseDto {
        return PiscinaAsignadaResponseDto(
            id = piscina.id,
            nombre = piscina.nombre,
            esDesbordante = piscina.esDesbordante,
            volumen = piscina.volumen.toString(),
        )
    }

    fun buildPiscinaResponseDto(piscina: Piscina): PiscinaResponseDto {
        return PiscinaResponseDto(
            id = piscina.id,
            nombre = piscina.nombre,
            direccion = piscina.direccion,
            ciudad = piscina.ciudad,
            esDesbordante = piscina.esDesbordante,
            largo = piscina.largo,
            ancho = piscina.ancho,
            profundidad = piscina.profundidad,
            volumen = piscina.volumen,
            volumenTC = piscina.volumenTC,
            bomba = piscina.bomba.map { BombaMapper.buildBombaResponseDto(it) },
            filtro = FiltroMapper.buildFiltroResponseDto(piscina.filtro),
            valvulas = piscina.valvulas.map { ValvulaMapper.buildValvulaResponseDto(it) },
            sistemaGermicida = piscina.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(it) },
            calefaccion = piscina.calefaccion?.let { CalefaccionMapper.buildCalefaccionResponseDto(it) },
            cloroSalino = piscina.cloroSalino,
            controlAutomaticoPH = piscina.controlAutomaticoPH,
            orp = piscina.orp
        )
    }

    fun buildPiscina (piscinaDTO: PiscinaRequestDto) : Piscina {
        return Piscina(
            id = piscinaDTO.id,
            nombre = piscinaDTO.nombre,
            direccion = piscinaDTO.direccion,
            ciudad = piscinaDTO.ciudad,
            esDesbordante = piscinaDTO.esDesbordante,
            largo = piscinaDTO.largo,
            ancho = piscinaDTO.ancho,
            profundidad = piscinaDTO.profundidad,
            volumen = piscinaDTO.volumen,
            volumenTC = piscinaDTO.volumenTC,
            bomba = piscinaDTO.bomba.map { BombaMapper.buildBomba(it) }.toMutableList(),
            filtro = FiltroMapper.buildFiltro(piscinaDTO.filtro),
            valvulas = piscinaDTO.valvulas.map { ValvulaMapper.buildValvula(it) }.toMutableSet(),
            sistemaGermicida = piscinaDTO.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicida(it) }.toMutableSet(),
            calefaccion = piscinaDTO.calefaccion?.let { CalefaccionMapper.buildCalefaccion(it) },
            cloroSalino = piscinaDTO.cloroSalino,
            controlAutomaticoPH = piscinaDTO.controlAutomaticoPH,
            orp = piscinaDTO.orp,
        )
    }

}