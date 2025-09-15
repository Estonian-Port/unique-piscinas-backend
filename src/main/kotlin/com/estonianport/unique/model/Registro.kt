package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Registro(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column (nullable = false)
    var fecha: LocalDate,

    @Column
    var dispositivo : String,

    @Column
    var descripcion: String,

    @Column
    var nombreTecnico : String,

    @Column
    var accion: String,
)