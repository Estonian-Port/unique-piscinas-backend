package com.estonianport.unique.dto.response

data class PiscinaAsignadaResponseDto (
    val id: Long,
    val nombre: String,
    val esDesbordante: Boolean,
    val volumen: String,
)