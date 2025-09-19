package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.ProgramacionType

data class ProgramacionResponseDto (
    val id: String,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
    val tipo: ProgramacionType,
)