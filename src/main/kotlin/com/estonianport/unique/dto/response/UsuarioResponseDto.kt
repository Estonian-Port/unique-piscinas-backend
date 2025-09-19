package com.estonianport.unique.dto.response

data class UsuarioResponseDto (
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val celular: Long,
    val isAdmin: Boolean,
    val piscinasId: List<Long>,
    val primerLogin: Boolean
)