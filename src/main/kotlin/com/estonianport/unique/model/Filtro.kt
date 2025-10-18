package com.estonianport.unique.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import java.time.LocalDate
import java.time.Period

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Filtro(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var marca: String,
    var modelo: String,
    var diametro: Double,
    var activo: Boolean = true,
    var tiempoDeVidaUtil: Int,
    var fechaAlta: LocalDate = LocalDate.now(),
) {
    fun vidaRestante(): Int? {
        val fechaVencimiento = fechaAlta.plusYears(tiempoDeVidaUtil.toLong())
        val ahora = LocalDate.now()
        if (ahora.isAfter(fechaVencimiento)) return 0 // ya venció
        val mesesRestantes = Period.between(ahora, fechaVencimiento).toTotalMonths()
        return if (mesesRestantes <= 3) mesesRestantes.toInt() else null
    }
}

@DiscriminatorValue("ARENA")
@Entity
class FiltroArena(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    tiempoDeVidaUtil: Int,
    fechaAlta: LocalDate = LocalDate.now(),
    var cantidadArena: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, fechaAlta)

@DiscriminatorValue("VIDRIO")
@Entity
class FiltroVidrio(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    tiempoDeVidaUtil: Int,
    fechaAlta: LocalDate = LocalDate.now(),
    var cantidadVidrio: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, fechaAlta)

@DiscriminatorValue("CARTUCHO")
@Entity
class FiltroCartucho(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    tiempoDeVidaUtil: Int,
    fechaAlta: LocalDate = LocalDate.now(),
    var micrasDelCartucho: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, fechaAlta)



