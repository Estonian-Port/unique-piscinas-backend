package com.estonianport.unique.dto.request

class SistemaGermicidaRequestDto (
    val id: Long,
    val tipo: String, // SistemaGermicidaType
    val activo: Boolean,
    val marca: String,
    val datoExtra: Double // Potencia para UV y US, Electrodos para Ionizador
)