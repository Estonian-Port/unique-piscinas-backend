package com.estonianport.unique.dto.request

data class CalefaccionRequestDto (
    val id: Long?,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val potencia: Double,
    val activa: Boolean
)