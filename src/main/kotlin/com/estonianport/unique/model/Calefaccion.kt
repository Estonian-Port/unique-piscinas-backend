package com.estonianport.unique.model

import com.estonianport.unique.model.enums.CalefaccionType
import jakarta.persistence.*

@Entity
class Calefaccion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    @Enumerated(EnumType.STRING)
    val tipo: CalefaccionType,

    @Column
    var potencia: Double,

    @Column
    var modelo: String,

    @Column
    var marca: String

)