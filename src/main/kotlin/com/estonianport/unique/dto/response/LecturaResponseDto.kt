package com.estonianport.unique.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
data class LecturaResponseDto(
    @JsonProperty("id_solicitud")
    val idSolicitud: String? = null,
    @JsonProperty("Patente")
    val patente: String,
    @JsonProperty("Redos")
    val redox: Float,
    @JsonProperty("Ph")
    val ph: Float,
    @JsonProperty("Cloro")
    val cloro: Float,
    @JsonProperty("PresTrF")
    val presion: Float,
    @JsonProperty("Temp")
    val temperatura: Float,
    @JsonProperty("Humedad")
    val humedad: Float,
    @JsonProperty("TempAgua")
    val temperaturaAgua: Float,
    @JsonProperty("EAA")
    val eaa: Int,
    @JsonProperty("SistGerm")
    val sistGerm: Int,
    @JsonProperty("FFA")
    val ffa: Int,
    @JsonProperty("CaAc")
    val caAc: Boolean
)
