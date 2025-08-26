package com.estonianport.unique.dto.response

data class PiscinaResponseDto (
    val id: Long,
    val direccion: String,
    val ciudad: String,
    val esDesbordante: Boolean,
    val largo: Double,
    val ancho: Double,
    val profundidad: Double,
    val volumen: Double,
    val volumenTC: Double?,
    val bomba: List<BombaResponseDto>,
    val filtro: FiltroResponseDto,
    val valvulas: List<ValvulaResponseDto>,
    val sistemaGermicida: List<SistemaGermicidaResponseDto>,
    val calefaccion: CalefaccionResponseDto?,
    val cloroSalino: Boolean,
    val controlAutomaticoPH: Boolean,
    val orp: Boolean,
)