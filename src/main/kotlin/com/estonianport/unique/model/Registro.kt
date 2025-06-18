package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Registro(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val fecha: LocalDate,

    @Column
    val descripcion: String,

    @Column
    val tecnico: Usuario,

    @Column
    val accion: String,
)