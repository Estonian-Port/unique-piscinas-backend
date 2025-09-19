package com.estonianport.unique.dto.response

import com.estonianport.unique.model.Registro

data class PiscinaEquiposDto(
    val id: Long,
    val direccion: String,
    val bombas: List<BombaResponseDto>,
    val filtro: FiltroResponseDto,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
    val cloroSalino: Boolean,
    val controlAutomaticoPH: Boolean,
    val orp: Boolean,
    val calefaccion: CalefaccionResponseDto?,
    val registros: List<RegistroResponseDto>,
)