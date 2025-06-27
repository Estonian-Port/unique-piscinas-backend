package com.estonianport.unique.dto.response

import java.time.LocalDateTime

data class LecturaConErrorResponseDto(
    val id: Long,
    val fecha: LocalDateTime,
    val ph: Double?,
    val cloro: Double?,
    val temperatura: Double?,
    val presion: Double?,
    val esError: Boolean
)

