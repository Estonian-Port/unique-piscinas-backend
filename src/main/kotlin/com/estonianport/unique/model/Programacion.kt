package com.estonianport.unique.model

import com.estonianport.unique.model.enums.FuncionFiltro
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Programacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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
    var activa: Boolean,
)

@DiscriminatorValue("ILUMINACION")
@Entity
class ProgramacionIluminacion(
    id: Long?,
    horaInicio: LocalTime,
    horaFin: LocalTime,
    dias: MutableList<DayOfWeek> = mutableListOf(),
    activa: Boolean,
) : Programacion(
    id = id,
    horaInicio = horaInicio,
    horaFin = horaFin,
    dias = dias,
    activa = activa,
)

@DiscriminatorValue("FILTRADO")
@Entity
class ProgramacionFiltrado(
    id: Long?,
    horaInicio: LocalTime,
    horaFin: LocalTime,
    dias: MutableList<DayOfWeek> = mutableListOf(),
    activa: Boolean,

    @Column
    @Enumerated(EnumType.STRING)
    var funcionFiltro: FuncionFiltro = FuncionFiltro.FILTRAR,
) : Programacion(
    id = id,
    horaInicio = horaInicio,
    horaFin = horaFin,
    dias = dias,
    activa = activa,
)
