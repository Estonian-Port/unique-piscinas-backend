package com.estonianport.unique.model

import com.estonianport.unique.model.enums.*
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
class EstadoPiscina(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id", nullable = false)
    val piscina: Piscina,

    @ElementCollection
    @CollectionTable(
        name = "piscina_entrada_agua_activa",
        joinColumns = [JoinColumn(name = "piscina_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "entrada_agua_activa")
    val entradaAguaActiva: MutableList<EntradaAguaType>,

    @Column
    @Enumerated(EnumType.STRING)
    var funcionFiltroActivo: FuncionFiltroType,

    @Column(nullable = false)
    var fecha: LocalDateTime = LocalDateTime.now(),

    @Column
    var luces: Boolean = false,

    ) {
    var inicioTrabajoFiltro: LocalDateTime = LocalDateTime.now()
    var finTrabajoFiltro: LocalDateTime = LocalDateTime.now()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    var ultimaActividad: LocalDateTime? = null

    fun activarFuncionFiltro(funcion: FuncionFiltroType) {
        if (entradaAguaActiva.isNotEmpty()) {
            funcionFiltroActivo = funcion
            if (funcion == FuncionFiltroType.FILTRAR || funcion == FuncionFiltroType.RECIRCULAR) {
                inicioTrabajoFiltro = LocalDateTime.now()
                piscina.activarSistemasGermicidas()
            } else {
                piscina.desactivarSistemasGermicidas(calcularTiempoUsoGermicidas())
            }
        }
    }

    fun desactivarFuncionFiltro() {
        if (funcionFiltroActivo == FuncionFiltroType.FILTRAR || funcionFiltroActivo == FuncionFiltroType.RECIRCULAR) {
            finTrabajoFiltro = LocalDateTime.now()
            piscina.desactivarSistemasGermicidas(calcularTiempoUsoGermicidas())
        }
        funcionFiltroActivo = FuncionFiltroType.REPOSO
        ultimaActividad = LocalDateTime.now()
    }

    fun calcularTiempoUsoGermicidas(): Int {
        val minutosUso = Duration.between(inicioTrabajoFiltro, LocalDateTime.now()).toMinutes().toInt()
        return minutosUso
    }

    fun copy() : EstadoPiscina {
        return EstadoPiscina(
            piscina = piscina,
            entradaAguaActiva = entradaAguaActiva.toMutableList(),
            funcionFiltroActivo = funcionFiltroActivo,
            fecha = LocalDateTime.now(),
            luces = luces
        )
    }

    companion object {
        fun estadoInicial(piscina: Piscina): EstadoPiscina {
            return EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = mutableListOf(),
                funcionFiltroActivo = FuncionFiltroType.REPOSO,
                fecha = LocalDateTime.now()
            )
        }
    }
}

