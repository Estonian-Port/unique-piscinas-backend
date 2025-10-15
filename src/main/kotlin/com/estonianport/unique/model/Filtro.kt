package com.estonianport.unique.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import java.time.Duration

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
    var tiempoDeVidaUtil: Duration = Duration.ZERO,
    var vidaRestante: Duration = tiempoDeVidaUtil,
) {
    fun filtroUsado (tiempoUsado : Duration) {
        vidaRestante -= tiempoUsado
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
    tiempoDeVidaUtil: Duration,
    vidaRestante: Duration,
    var cantidadArena: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, vidaRestante)

@DiscriminatorValue("VIDRIO")
@Entity
class FiltroVidrio(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    tiempoDeVidaUtil: Duration,
    vidaRestante: Duration,
    var cantidadVidrio: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, vidaRestante)

@DiscriminatorValue("CARTUCHO")
@Entity
class FiltroCartucho(
    id: Long,
    marca: String,
    modelo: String,
    diametro: Double,
    activo: Boolean = true,
    tiempoDeVidaUtil: Duration,
    vidaRestante: Duration,
    var micrasDelCartucho: Int
) : Filtro(id, marca, modelo, diametro, activo, tiempoDeVidaUtil, vidaRestante)



