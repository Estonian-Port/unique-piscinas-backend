package com.estonianport.unique.model

import com.estonianport.unique.model.enums.CondicionType
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class SistemaGermicida(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    var activo: Boolean = false,
    var marca: String,
    var tiempoVidaUtil: Int,
    @Enumerated(EnumType.STRING)
    var condicion: CondicionType? = null
) {
    var vidaRestante: Int = tiempoVidaUtil * 60 // Vida util en minutos

    fun descontarVida(tiempoUso : Int) { // tiempoUso en minutos
        vidaRestante -= tiempoUso
        if (vidaRestante < 0) vidaRestante = 0
        verificarEstado()
        activo = false
    }

    fun resetearVida() {
        vidaRestante = tiempoVidaUtil * 60
        verificarEstado()
    }

    fun verificarEstado() {
        condicion = if (vidaRestante > (50 * 60)) {
            CondicionType.OPERATIVO
        } else if (vidaRestante in (25 * 60)..(50 * 60)) {
            CondicionType.REQUIERE_REVISION
        } else if (vidaRestante in (1 * 60)..(24 * 60)) {
            CondicionType.REEMPLAZO_URGENTE
        } else {
            CondicionType.MANTENIMIENTO
        }
    }

    fun tipo(): String {
        return when (this) {
            is UV -> "UV"
            is Ionizador -> "Ionizador de cobre"
            is Trasductor -> "Trasductor de ultrasonido"
            else -> "Desconocido"
        }
    }
}

@DiscriminatorValue("UV")
@Entity
class UV(
    id: Long,
    activo: Boolean = false,
    marca: String,
    tiempoVidaUtil: Int,
    estado: CondicionType,
    var potencia: Double
) : SistemaGermicida(id, activo, marca, tiempoVidaUtil, estado)

@DiscriminatorValue("IONIZADOR")
@Entity
class Ionizador(
    id: Long,
    activo: Boolean = false,
    marca: String,
    tiempoVidaUtil: Int,
    estado: CondicionType,
    var electrodos: Double
) : SistemaGermicida(id, activo, marca, tiempoVidaUtil, estado)

@DiscriminatorValue("TRASDUCTOR")
@Entity
class Trasductor(
    id: Long,
    activo: Boolean = false,
    marca: String,
    tiempoVidaUtil: Int,
    estado: CondicionType,
    var potencia: Double
) : SistemaGermicida(id, activo, marca, tiempoVidaUtil, estado)