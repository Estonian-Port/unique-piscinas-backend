package com.estonianport.unique.dto.request

import com.estonianport.unique.model.enums.ProgramacionType

data class ProgramacionRequestDto (
    val id: Long,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
    val filtrado: Boolean,
    val tipo: ProgramacionType
)