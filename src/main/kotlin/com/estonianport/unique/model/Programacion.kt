package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
data class Programacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val horaInicio: LocalTime,

    @Column
    val horaFin: LocalTime,

    @ElementCollection
    @CollectionTable(
        name = "programacion_dia",
        joinColumns = [JoinColumn(name = "programacion_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    val dias: List<DayOfWeek>,

    @Column
    val estaActivo: Boolean
) {}
