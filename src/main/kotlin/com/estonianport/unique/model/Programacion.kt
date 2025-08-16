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
    var horaInicio: LocalTime,

    @Column
    var horaFin: LocalTime,

    @ElementCollection
    @CollectionTable(
        name = "programacion_dia",
        joinColumns = [JoinColumn(name = "programacion_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    var dias: MutableList<DayOfWeek> = mutableListOf(),

    @Column
    var estaActivo: Boolean
) {}
