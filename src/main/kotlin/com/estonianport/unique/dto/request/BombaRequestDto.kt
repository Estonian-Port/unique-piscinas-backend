package com.estonianport.unique.dto.request

import com.estonianport.unique.model.enums.EstadoType

data class BombaRequestDto (
    val id: Long,
    val marca: String,
    val modelo: String,
    val potencia: Double,
    val activa: Boolean,
    val estadoType: EstadoType
)