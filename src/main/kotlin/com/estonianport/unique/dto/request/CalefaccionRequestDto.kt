package com.estonianport.unique.dto.request

import com.estonianport.unique.model.enums.EstadoType

class CalefaccionRequestDto (
    val id: Long,
    val tipo: String,
    val potencia: Double,
    val modelo: String,
    val marca: String,
    var estado: EstadoType,
    val activa: Boolean
)