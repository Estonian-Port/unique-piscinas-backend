package com.estonianport.unique.dto.request

class CalefaccionRequestDto (
    val id: Long,
    val tipo: String,
    val potencia: Double,
    val modelo: String,
    val marca: String,
    var estado: Boolean
)