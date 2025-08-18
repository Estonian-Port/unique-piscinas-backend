package com.estonianport.unique.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Filtro(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val marca: String,
    val modelo: String,
    val diametro: Double,
    val activo: Boolean
)

@DiscriminatorValue("ARENA")
@Entity
class FiltroArena(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean,
    val cantidadArena: Int
) : Filtro(id, marca, modelo, diametro, activo)

@DiscriminatorValue("VIDRIO")
@Entity
class FiltroVidrio(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean,
    val cantidadVidrio: Int
) : Filtro(id, marca, modelo, diametro, activo)

@DiscriminatorValue("CARTUCHO")
@Entity
class FiltroCartucho(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean,
    val micrasDelCartucho: Int
) : Filtro(id, marca, modelo, diametro, activo) {

}

@DiscriminatorValue("DIATOMEA")
@Entity
class FiltroDiatomea(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean
) : Filtro(id, marca, modelo, diametro, activo)



