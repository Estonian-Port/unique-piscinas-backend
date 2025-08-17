package com.estonianport.unique.model

import com.estonianport.unique.model.enums.CalefaccionType
import com.estonianport.unique.model.enums.EstadoType
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
    val potencia: Double,

    @Column
    val modelo: String,

    @Column
    val marca: String,

    @Column
    var activa: Boolean,

    @Column
    @Enumerated(EnumType.STRING)
    var estado: EstadoType
) {
    fun cambiarEstado() {
        activa = !activa
    }
}