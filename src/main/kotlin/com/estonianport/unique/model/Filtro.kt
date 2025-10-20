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
    fun vidaRestante(): String {
        val fechaVencimiento = fechaAlta.plusYears(tiempoDeVidaUtil.toLong())
        val ahora = LocalDate.now()
        if (ahora.isAfter(fechaVencimiento)) return "Vencido"
        val period = Period.between(ahora, fechaVencimiento)
        val totalMeses = period.years * 12 + period.months
        if (totalMeses >= 12) {
            val anios = totalMeses / 12
            val meses = totalMeses % 12
            if (meses > 1) return "$anios años $meses meses"
            return if (meses == 1) "$anios años $meses mes" else "$anios años"
        }
        if (totalMeses > 1) return "$totalMeses meses"
        if (totalMeses == 1) return "$totalMeses mes"
        val dias = period.days
        return if (dias > 1) "$dias días" else if (dias == 1) "$dias día" else "Menos de un día"
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



