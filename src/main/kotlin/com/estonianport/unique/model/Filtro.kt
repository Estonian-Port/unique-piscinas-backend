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
    var id: Long,
    var marca: String,
    var modelo: String,
    var diametro: Double,
    var activo: Boolean = true
)

@DiscriminatorValue("ARENA")
@Entity
class FiltroArena(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    var cantidadArena: Int
) : Filtro(id, marca, modelo, diametro, activo)

@DiscriminatorValue("VIDRIO")
@Entity
class FiltroVidrio(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    var cantidadVidrio: Int
) : Filtro(id, marca, modelo, diametro, activo)

@DiscriminatorValue("CARTUCHO")
@Entity
class FiltroCartucho(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    var micrasDelCartucho: Int
) : Filtro(id, marca, modelo, diametro, activo)



