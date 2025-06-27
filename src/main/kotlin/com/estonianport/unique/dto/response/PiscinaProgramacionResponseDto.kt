package com.estonianport.unique.dto.response

data class PiscinaProgramacionResponseDto (
    val id: String,
    val nombre: String,
    val direccion: String,
    val volumen: String,
    val programacionLuces: List<ProgramacionResponseDto>,
    val programacionFiltrado: List<ProgramacionResponseDto>
)