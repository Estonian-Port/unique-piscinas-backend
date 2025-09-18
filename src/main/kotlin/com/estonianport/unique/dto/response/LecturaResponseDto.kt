package com.estonianport.unique.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LecturaResponseDTO(
    @JsonProperty("id_solicitud")
    val idSolicitud: String,
    val ph: Double,
    val cloro: Double,
    val temperatura: Double,
    val presion: Double,
    val timestamp: String
)