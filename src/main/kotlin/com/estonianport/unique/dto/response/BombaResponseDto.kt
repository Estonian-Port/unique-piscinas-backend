package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.EstadoType

data class BombaResponseDto (
    val id: String,
    val marca: String,
    val modelo: String,
    val potencia: String,
    val activa: Boolean,
)
