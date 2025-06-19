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

    // No estoy tan seguro si es correcto usar ElemnentCollection.
    @ElementCollection
    @Enumerated(EnumType.STRING)
    val dias: List<DayOfWeek>,

    @Column
    val estaActivo: Boolean
) {}
