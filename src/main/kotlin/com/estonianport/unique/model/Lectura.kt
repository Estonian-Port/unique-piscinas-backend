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
    val redox: Float,

    @Column(nullable = false)
    val ph: Float,

    @Column(nullable = false)
    val cloro: Float,

    @Column(nullable = false)
    val presion: Float,

    @Column(nullable = false)
    val temperatura: Float,

    @Column(nullable = false)
    val humedad: Float,

    @Column(nullable = false)
    val temperaturaAgua: Float,


) : LecturaBase(piscina)


@Entity
class ErrorLectura(
    piscina: Piscina

) : LecturaBase(piscina)

