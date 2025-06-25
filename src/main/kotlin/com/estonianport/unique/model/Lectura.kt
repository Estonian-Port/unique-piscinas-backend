package com.estonianport.unique.model

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class LecturaBase(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fecha: LocalDateTime
)


@Entity
class Lectura(

    id: Long = 0,
    fecha: LocalDateTime,

    @Column(nullable = false)
    val ph: Double,

    @Column(nullable = false)
    val cloro: Double,

    @Column(nullable = false)
    val temperatura: Double,

    @Column(nullable = false)
    val presion: Double

) : LecturaBase(id, fecha)


@Entity
class ErrorLectura(
    id: Long = 0,
    fecha: LocalDateTime

) : LecturaBase(id, fecha)

