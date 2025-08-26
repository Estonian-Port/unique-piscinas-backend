package com.estonianport.unique.dto.response

data class PiscinaResumenResponseDto (
    val id: String,
    val direccion: String,
    val volumen: String,
    val clima: String,
    val entradaAgua: List<String>,
    val funcionActiva: List<String>,
    val sistemasGermicidas: List<String>,
    val calefaccion: Boolean,
)