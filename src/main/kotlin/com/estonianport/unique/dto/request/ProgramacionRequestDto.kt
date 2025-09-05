package com.estonianport.unique.dto.request

import com.estonianport.unique.model.enums.FuncionFiltro

data class ProgramacionLucesRequestDto (
    val id: Long,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
)

data class ProgramacionFiltradoRequestDto (
    val id: Long,
    val horaInicio: String,
    val horaFin: String,
    val dias: List<String>,
    val activa: Boolean,
    val funcionFiltro: FuncionFiltro,
)