package com.estonianport.unique.model

import com.estonianport.unique.model.enums.CalefaccionType
import jakarta.persistence.*

@Entity
class Calefaccion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val tipo: CalefaccionType,

    @Column
    val potencia: Double,

    @Column
    val modelo: String,

    @Column
    val marca: String,

    @Column
    var estado: Boolean
) {
    fun cambiarEstado() {
        estado = !estado
    }
}