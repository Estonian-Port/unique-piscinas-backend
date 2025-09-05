package com.estonianport.unique.dto.request

data class PiscinaRequestDto(
    val id: Long?,
    val administradorId: Long?,
    val direccion: String,
    val ciudad: String,
    val codigoPlaca: String,
    val notas: String?,
    val esDesbordante: Boolean,
    val largo: Double,
    val ancho: Double,
    val profundidad: Double,
    val volumen: Double,
    val volumenTC: Double?,
    val bomba: List<BombaRequestDto>,
    val filtro: FiltroRequestDto,
    val cloroSalino: Boolean,
    val controlAutomaticoPH: Boolean,
    val orp: Boolean,
    val sistemaGermicida: List<SistemaGermicidaRequestDto>,
    val calefaccion: CalefaccionRequestDto?,
)