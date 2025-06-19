package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Lectura(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val fecha: LocalDate,

    @Column
    val hora: LocalTime,

    @Column
    val ph: Double,

    @Column
    val cloro: Double,

    @Column
    val temperatura: Double,

    @Column
    val presion: Double
) {
}