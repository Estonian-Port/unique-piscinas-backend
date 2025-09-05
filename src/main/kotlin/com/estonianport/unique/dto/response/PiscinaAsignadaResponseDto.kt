package com.estonianport.unique.dto.response

data class PiscinaAsignadaResponseDto (
    val id: Long,
    val direccion: String,
    val esDesbordante: Boolean,
    val volumen: String,
)