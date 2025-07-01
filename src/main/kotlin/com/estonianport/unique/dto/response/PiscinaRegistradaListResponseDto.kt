package com.estonianport.unique.dto.response

import com.estonianport.unique.model.SistemaGermicida

data class PiscinaRegistradaListResponseDto (
    val id: Long,
    val nombre: String,
    val esDesbordante: Boolean,
    val administradorNombre: String,
    val ph: Double,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
    val calefaccion: CalefaccionResponseDto?
)