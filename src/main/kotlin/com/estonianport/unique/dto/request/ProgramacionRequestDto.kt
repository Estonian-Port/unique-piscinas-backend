package com.estonianport.unique.dto.request


data class ProgramacionRequestDto (
    val id: Long,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
    val tipo: String,
    val idPiscina: Long,
)