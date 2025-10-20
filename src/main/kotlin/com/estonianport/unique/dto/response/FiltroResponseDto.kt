package com.estonianport.unique.dto.response

data class FiltroResponseDto (
    val id: Long,
    val tipo: String,
    val marca : String,
    val modelo: String,
    val diametro: Double,
    val activo: Boolean,
    val datoExtra: Double,
    val vidaRestante: String
)