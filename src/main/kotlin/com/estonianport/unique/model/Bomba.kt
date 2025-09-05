package com.estonianport.unique.model

import com.estonianport.unique.model.enums.EstadoType
import jakarta.persistence.*

@Entity
data class Bomba(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val marca: String,

    @Column
    val modelo: String,

    @Column
    val potencia: Double,

    @Column
    val activa: Boolean = false,
) {}