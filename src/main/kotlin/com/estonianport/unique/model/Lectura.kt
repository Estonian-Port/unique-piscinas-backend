package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class LecturaBase(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id", nullable = false)
    val piscina: Piscina){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false)
    val fecha: LocalDateTime = LocalDateTime.now()

}


@Entity
class Lectura(
    piscina: Piscina,

    @Column(nullable = false)
    val ph: Double,

    @Column(nullable = false)
    val cloro: Double,

    @Column(nullable = false)
    val temperatura: Double,

    @Column(nullable = false)
    val presion: Double

) : LecturaBase(piscina)


@Entity
class ErrorLectura(
    piscina: Piscina

) : LecturaBase(piscina)

