package com.estonianport.unique.dto.request

data class PiscinaRequestDto(
    val piscinaId: String,
    val nombre: String,
    val direccion: String,
    val ciudad: String,
    val esDesbordante: Boolean,
    val largo: Double,
    val ancho: Double,
    val profundidad: Double,
    val volumen: Double,
    val volumenTC: Double?,
    val bomba: List<Long>, // Lista de bombaRequestDto
    val filtro: String, // FiltroRequestDto
    val valvulas: List<Long>, // Lista de valvulaRequestDto
    val sistemaGermicida: List<Long> // Lista de sistemaGermicidaRequestDto
    val calefaccion: String?, // CalefaccionRequestDto
    val cloroSalino: Boolean,
    val controlAutomaticoPH: Boolean,
    val orp: Boolean
    val propietario: String?, // PropietarioRequestDto
)