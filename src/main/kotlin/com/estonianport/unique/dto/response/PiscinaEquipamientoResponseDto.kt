package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.FuncionFiltroType

data class PiscinaEquipamientoResponseDto(
    val id: String,
    val direccion: String,
    val volumen: String,
    val estadoFiltro: Boolean,
    val entradaAgua: List<String>,
    val funcionActiva: FuncionFiltroType?,
    val presion: Double,
    val bombas: List<BombaResponseDto>,
    val filtro: FiltroResponseDto,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
)