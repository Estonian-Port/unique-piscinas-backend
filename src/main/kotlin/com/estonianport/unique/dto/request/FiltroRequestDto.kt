package com.estonianport.unique.dto.request

data class FiltroRequestDto (
    val id: Long,
    val marca: String,
    val modelo: String,
    val diametro: Double,
    val activo: Boolean,
    val cantidadArena: Int?,
    val cantidadVidrio: Int?,
    val micrasDelCartucho: Int?,
)