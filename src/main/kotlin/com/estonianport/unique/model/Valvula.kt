package com.estonianport.unique.model

import com.estonianport.unique.model.enums.CondicionType
import com.estonianport.unique.model.enums.ValvulaType
import jakarta.persistence.*

@Entity
class Valvula(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    @Enumerated(EnumType.STRING)
    val tipo: ValvulaType,

    @Column
    @Enumerated(EnumType.STRING)
    val estado: CondicionType,
) {

    fun notificarReemplazo() {
        // Habria que enviar un mensaje a la placa controladora para notificar el reemplazo.
    }

    fun estado(): String {
        // Implementación de la lógica para obtener el estado de la valvula
        // En este caso no habla de vida restante por lo que habria que ver como
        // se define el estado de la válvula. Los estados que aparecen en el prototipo son:
        // "Operativa", "Requiere revisión" y "Reemplazo urgente".
        return "Operativa"
    }
}