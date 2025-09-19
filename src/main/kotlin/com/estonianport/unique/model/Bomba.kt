package com.estonianport.unique.model

import com.estonianport.unique.model.enums.EstadoType
import jakarta.persistence.*

@Entity
data class Bomba(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    var marca: String,

    @Column
    var modelo: String,

    @Column
    var potencia: Double,

    @Column
    var activa: Boolean = false,
) {}