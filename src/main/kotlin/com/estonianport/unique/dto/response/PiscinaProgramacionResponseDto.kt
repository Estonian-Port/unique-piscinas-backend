package com.estonianport.unique.dto.response

data class PiscinaProgramacionResponseDto (
    val id: String,
    val direccion: String,
    val volumen: String,
    val programacionIluminacion: List<ProgramacionIluminacionResponseDto>,
    val programacionFiltrado: List<ProgramacionFiltradoResponseDto>
)