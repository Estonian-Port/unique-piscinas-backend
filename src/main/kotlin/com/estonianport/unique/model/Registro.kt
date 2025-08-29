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
    val dispositivo : String,

    @Column
    val descripcion: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    val tecnico: Usuario,

    @Column
    val nombreTecnico : String,

    @Column
    val accion: String,
)