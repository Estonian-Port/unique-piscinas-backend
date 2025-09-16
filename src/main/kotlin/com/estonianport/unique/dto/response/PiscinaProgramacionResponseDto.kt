package com.estonianport.unique.dto.response

data class PiscinaProgramacionResponseDto (
    val id: String,
    val direccion: String,
    val volumen: String,
    val programacionIluminacion: List<ProgramacionResponseDto>,
    val programacionFiltrado: List<ProgramacionResponseDto>
)