package com.estonianport.unique.dto.response

data class BombaResponseDto (
    val id: String,
    val marca: String,
    val modelo: String,
    val potencia: String,
    val activa: Boolean,
    val tipo: String,
)
