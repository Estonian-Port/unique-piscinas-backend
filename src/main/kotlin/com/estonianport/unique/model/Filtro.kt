package com.estonianport.unique.model

interface Filtro {
    val marca: String
    val modelo: String
    val diametro: Double
}

data class FiltroArena(
    override val marca: String,
    override val modelo: String,
    override val diametro: Double,
    val cantidadArena: Int
) : Filtro

data class FiltroVidrio(
    override val marca: String,
    override val modelo: String,
    override val diametro: Double,
    val cantidadVidrio: Int
) : Filtro

data class FiltroCartucho(
    override val marca: String,
    override val modelo: String,
    override val diametro: Double,
    val micrasDelCartucho: Int
) : Filtro

data class FiltroDiatomea(
    override val marca: String,
    override val modelo: String,
    override val diametro: Double
) : Filtro