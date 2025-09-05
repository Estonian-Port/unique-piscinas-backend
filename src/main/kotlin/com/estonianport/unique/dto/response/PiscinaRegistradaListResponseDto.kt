package com.estonianport.unique.dto.response

data class PiscinaRegistradaListResponseDto (
    val id: Long,
    val direccion: String,
    val esDesbordante: Boolean,
    val nombreAdministrador: String,
    val ph: Double,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
)