package com.estonianport.unique.model

import com.estonianport.unique.model.enums.ProgramacionType
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "programaciones")
class Programacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "hora_inicio", nullable = false)
    var horaInicio: LocalTime,

    @Column(name = "hora_fin", nullable = false)
    var horaFin: LocalTime,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "programacion_dia",
        joinColumns = [JoinColumn(name = "programacion_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    var dias: MutableList<DayOfWeek> = mutableListOf(),

    @Column(nullable = false)
    var activa: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipo: ProgramacionType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    var piscina: Piscina? = null,

    @Column
    var pausadaManualmente: Boolean = false
) {
    // 🔹 Campo transient: no se guarda en BD, se calcula siempre
    @Transient
    var ejecutando: Boolean = false

    // 🔹 Se ejecuta automáticamente al cargar desde BD
    @PostLoad
    fun calcularEstado() {
        ejecutando = activa && !pausadaManualmente && esAhora()
    }

    fun esAhora(): Boolean {
        val ahora = LocalTime.now()
        val diaHoy = LocalDate.now().dayOfWeek
        return dias.contains(diaHoy) && ahora >= horaInicio && ahora < horaFin
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Programacion) return false

        // Si ambos tienen ID, comparamos por ID (modo persistente)
        if (id != null && other.id != null) {
            return id == other.id
        }

        // Si no tienen ID (modo "transitorio"), comparamos por contenido relevante
        return horaInicio == other.horaInicio &&
                horaFin == other.horaFin &&
                dias == other.dias &&
                tipo == other.tipo
    }

    override fun hashCode(): Int {
        // Si tiene ID, el hash depende de él (consistente con equals)
        return id?.hashCode() ?: listOf(horaInicio, horaFin, dias, tipo).hashCode()
    }
}
