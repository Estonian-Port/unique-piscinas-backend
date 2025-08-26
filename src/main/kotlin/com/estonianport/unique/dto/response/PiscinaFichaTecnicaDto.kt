package com.estonianport.unique.dto.response

data class PiscinaFichaTecnicaDto (
    val id: Long,
    val direccion: String,
    val ciudad: String,
    val nombreAdministrador: String,
    val placaId: Long,
    val esDesbordante: Boolean,
    val largo: Double,
    val ancho: Double,
    val profundidad: Double,
    val volumen: Double,
    val volumenTC: Double?,
    val notas: String?,
)