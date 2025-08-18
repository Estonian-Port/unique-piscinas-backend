package com.estonianport.unique.model

import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.estonianport.unique.model.enums.EstadoType
import jakarta.persistence.*

@Entity
class SistemaGermicida(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    @Enumerated(EnumType.STRING)
    val tipo: SistemaGermicidaType,

    @Column
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
        } else if (vidaRestante() in 20..50) {
            "Atención"
        } else {
            "Reemplazo urgente"
        }
    }
}
