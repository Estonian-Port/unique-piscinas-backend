package com.estonianport.unique.model

import com.estonianport.unique.model.enums.EstadoType
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class SistemaGermicida(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val activo: Boolean =true,
    var marca: String,
    @Enumerated(EnumType.STRING)
    val estado: EstadoType
) {
    fun vidaRestante(): Int {
        // Implementación de la lógica para calcular la vida restante del sistema germicida
        // Entiendo que la placa controladora es la que dara esta información.
        return 50
    }

    fun resetearVida() {
        // Implementación de la lógica para resetear la vida del sistema germicida
        // Habria que enviar un mensaje a la placa controladora para resetear la vida.
    }

    fun estado(): String {
        // Implementación de la lógica para obtener el estado del sistema germicida
        // Hay que definir puntos de corte para definir el estado segun la vida restante.
        // Dejo planteado una posible implementación con 3 estados: "Optimo", "Atención" y "Reemplazo urgente".
        return if (vidaRestante() > 50) {
            "Optimo"
        } else if (vidaRestante() in 5..50) {
            "Requiere mantenimiento"
        } else {
            "Reemplazo urgente"
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
    activo: Boolean = true,
    marca: String,
    estado: EstadoType,
    var potencia: Double
) : SistemaGermicida(id, activo, marca, estado)

@DiscriminatorValue("IONIZADOR")
@Entity
class Ionizador(
    id: Long,
    activo: Boolean = true,
    marca: String,
    estado: EstadoType,
    var electrodos: Double
) : SistemaGermicida(id, activo, marca, estado)

@DiscriminatorValue("TRASDUCTOR")
@Entity
class Trasductor(
    id: Long,
    activo: Boolean = true,
    marca: String,
    estado: EstadoType,
    var potencia: Double
) : SistemaGermicida(id, activo, marca, estado)