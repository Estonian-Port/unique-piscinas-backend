package com.estonianport.unique.model

import com.estonianport.unique.model.enums.ProgramacionType
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "programaciones")
class Programacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "hora_inicio", nullable = false)
    var horaInicio: LocalTime,

    @Column(name = "hora_fin", nullable = false)
    var horaFin: LocalTime,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "programacion_dia",
        joinColumns = [JoinColumn(name = "programacion_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    var dias: MutableList<DayOfWeek> = mutableListOf(),

    @Column(nullable = false)
    var activa: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipo: ProgramacionType,

    @Column(name = "proxima_ejecucion")
    var proximaEjecucion: LocalDateTime? = null,
)
