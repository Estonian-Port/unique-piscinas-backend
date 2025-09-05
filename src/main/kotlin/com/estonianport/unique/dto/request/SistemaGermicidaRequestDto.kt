package com.estonianport.unique.dto.request

data class SistemaGermicidaRequestDto (
    val id: Long?,
    val tipo: String, // SistemaGermicidaType
    val marca: String,
    val activa: Boolean,
    val tiempoVidaUtil: Int,
    val datoExtra: Double // Potencia para UV y US, Electrodos para Ionizador
)