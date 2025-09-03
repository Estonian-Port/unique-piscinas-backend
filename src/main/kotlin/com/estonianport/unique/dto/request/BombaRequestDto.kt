package com.estonianport.unique.dto.request

data class BombaRequestDto (
    val id: Long?,
    val marca: String,
    val modelo: String,
    val potencia: Double,
    val esVelocidadVariable: Boolean,
    val activa: Boolean,
)