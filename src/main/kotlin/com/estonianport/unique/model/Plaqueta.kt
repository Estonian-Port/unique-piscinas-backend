package com.estonianport.unique.model

import com.estonianport.unique.model.enums.EstadoType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class Plaqueta(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val patente: String,

    @Column
    @Enumerated(EnumType.STRING)
    var estado: EstadoType = EstadoType.PENDIENTE,

    @Column
    var firmware: String,

    @Column
    var tipo : String,

    @OneToOne(mappedBy = "plaqueta", fetch = FetchType.LAZY, optional = true)
    var piscina: Piscina? = null

)