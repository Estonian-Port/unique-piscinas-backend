package com.estonianport.unique.dto.response

data class EstadisticasResponseDto (
    val totalUsuarios: Int,
    val usuariosActivos: Int,
    val usuariosInactivos: Int,
    val totalPiscinas: Int,
    val piscinasSkimmer: Int,
    val piscinasDesborde: Int,
    val volumenTotal: Double,
    val volumenPromedio: Double
)