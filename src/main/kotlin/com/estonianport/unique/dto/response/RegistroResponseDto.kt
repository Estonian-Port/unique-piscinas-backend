package com.estonianport.unique.dto.response

data class RegistroResponseDto (
    val id: Long,
    val fecha: String,
    val dispositivo: String,
    val accion: String,
    val descripcion: String,
    val nombreTecnico: String,
    )