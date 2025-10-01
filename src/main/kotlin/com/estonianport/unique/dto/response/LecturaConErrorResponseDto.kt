package com.estonianport.unique.dto.response

import java.time.LocalDateTime

data class LecturaConErrorResponseDto(
    val id: Long,
    val fecha: String,
    val ph: Double?,
    val cloro: Double?,
    val redox: Double?,
    val presion: Double?,
    val esError: Boolean
)

