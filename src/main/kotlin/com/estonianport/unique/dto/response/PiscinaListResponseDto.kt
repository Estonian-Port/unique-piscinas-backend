package com.estonianport.unique.dto.response

data class PiscinaListResponseDto (
    val id : String,
    val nombre: String,
    val direccion: String,
    val volumen: Double = 0.0
)
