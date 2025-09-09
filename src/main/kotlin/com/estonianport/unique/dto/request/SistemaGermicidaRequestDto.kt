package com.estonianport.unique.dto.request

data class SistemaGermicidaRequestDto (
    val id: Long?,
    val tipo: String, // SistemaGermicidaType
    val marca: String,
    val vidaRestante: Int,
    val activo: Boolean,
    val estado: String, // EstadoType
    val datoExtra: Double // Potencia para UV y US, Electrodos para Ionizador
)