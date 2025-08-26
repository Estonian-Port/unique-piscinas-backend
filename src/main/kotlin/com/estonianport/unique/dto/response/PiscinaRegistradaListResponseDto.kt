package com.estonianport.unique.dto.response

import com.estonianport.unique.model.SistemaGermicida

data class PiscinaRegistradaListResponseDto (
    val id: Long,
    val direccion: String,
    val esDesbordante: Boolean,
    val administradorNombre: String,
    val ph: Double,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
)