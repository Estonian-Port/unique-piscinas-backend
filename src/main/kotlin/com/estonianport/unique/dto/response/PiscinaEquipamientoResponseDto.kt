package com.estonianport.unique.dto.response

data class PiscinaEquipamientoResponseDto(
    val id: String,
    val nombre: String,
    val direccion: String,
    val volumen: String,
    // Los datos anteriores son comunes, no estoy seguro de pasarlos siempre.
    val estadoFiltro: Boolean,
    val entradaAgua: List<String>,
    val funcionActiva: List<String>,
    val presion: String,
    // Los siguientes datos no se si se refieren a lecturas o actividad del filtro. Si es actividad del filtro, ese dato de donde lo obtendriamos?
    // val ultimaActividad : String,
    // val proximoCiclo : String,
    val bombas: List<BombaResponseDto>,
    val filtro: FiltroResponseDto,
    val valvulas: List<ValvulaResponseDto>,
    val sistemasGermicidas: List<SistemaGermicidaResponseDto>,
)