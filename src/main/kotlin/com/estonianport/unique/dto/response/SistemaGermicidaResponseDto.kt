package com.estonianport.unique.dto.response

data class SistemaGermicidaResponseDto (
    val id: String,
    val tipo: String,
    val marca: String,
    val vidaRestante: String,
    val activo: Boolean,
    val estado: String,
    val datoExtra: Double
)