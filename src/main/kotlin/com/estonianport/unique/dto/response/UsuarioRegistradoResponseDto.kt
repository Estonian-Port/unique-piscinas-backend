package com.estonianport.unique.dto.response

data class UsuarioRegistradoResponseDto (
    val id: Long,
    val nombre: String,
    val apellido: String,
    val celular: Long,
    val email: String,
    val estado: String,
    val piscinasAsignadas: List<PiscinaAsignadaResponseDto>
)