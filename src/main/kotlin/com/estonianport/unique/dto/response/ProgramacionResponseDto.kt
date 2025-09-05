package com.estonianport.unique.dto.response

data class ProgramacionIluminacionResponseDto (
    val id: String,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
)

data class ProgramacionFiltradoResponseDto (
    val id: String,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
    val funcionFiltro: String,
)