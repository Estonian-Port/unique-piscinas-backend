package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.FuncionFiltroType

data class PiscinaResumenResponseDto (
    val id: String,
    val direccion: String,
    val volumen: String,
    val clima: String,
    val entradaAgua: List<String>,
    val funcionActiva: FuncionFiltroType?,
    val bombas: List<BombaResponseDto>?,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>?,
    val calefaccion: CalefaccionResponseDto?,
    val esDesbordante: Boolean,
)