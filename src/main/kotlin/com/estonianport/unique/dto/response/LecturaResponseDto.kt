package com.estonianport.unique.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LecturaResponseDto(

    @JsonProperty("id_solicitud")
    val idSolicitud: String? = null,

    @JsonProperty("patente")
    val patente: String,

    @JsonProperty("redox")
    val redox: Float,

    @JsonProperty("ph")
    val ph: Float,

    @JsonProperty("cloro")
    val cloro: Float,

    @JsonProperty("presion")
    val presion: Float,

    @JsonProperty("temperatura_agua")
    val temperaturaAgua: Float
)