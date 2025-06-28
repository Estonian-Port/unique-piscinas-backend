package com.estonianport.unique.dto.response

data class ProgramacionResponseDto (
    val id: String,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val estaActivo: Boolean
)