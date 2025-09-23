package com.estonianport.unique.dto.request

data class FiltroRequestDto(
    val id: Long?,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val activo: Boolean,
    val diametro: Double,
    val datoExtra: Int,
    val tiempoDeVidaUtil: Int
)

data class FuncionFiltroRequestDto(val funcion: String)