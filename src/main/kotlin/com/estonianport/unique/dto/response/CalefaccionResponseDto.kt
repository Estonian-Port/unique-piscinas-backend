package com.estonianport.unique.dto.response

data class CalefaccionResponseDto (
    val id: Long,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val potencia: Double,
    val activa: Boolean
)