package com.estonianport.unique.model

import com.estonianport.unique.model.enums.UsuarioType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Plaqueta(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val patente: String,

    @Column
    @Enumerated(EnumType.STRING)
    var estado: UsuarioType = UsuarioType.PENDIENTE,

    @Column
    var firmware: String,

    @Column
    var tipo : String


)