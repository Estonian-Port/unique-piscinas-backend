package com.estonianport.unique.model

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
    val esVelocidadVariable: Boolean = false,

    @Column
    val estaActiva: Boolean = true // Hay que ver si con un booleano alcanza o hay que trabajar con multiples estados como en los sitemas germicidas
) {}