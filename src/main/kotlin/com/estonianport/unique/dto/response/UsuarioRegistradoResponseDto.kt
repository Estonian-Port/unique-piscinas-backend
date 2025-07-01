package com.estonianport.unique.dto.response

data class UsuarioRegistradoResponseDto (
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    //val activo: Boolean,  ESTO NO ME ACUERDO QUE LO DEFINÍA
    val piscinasAsignadas: List<PiscinaAsignadaResponseDto>
)