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

    @ElementCollection
    @CollectionTable(
        name = "piscina_sistema_germicida_activo",
        joinColumns = [JoinColumn(name = "piscina_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_germicida_activo")
    val sistemaGermicidaActivo: List<SistemaGermicidaType>?,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val sistemasGermicida: MutableSet<SistemaGermicida>,

    @Column
    val calefaccionActiva: Boolean,

    @Column(nullable = false)
    val fecha: LocalDateTime = LocalDateTime.now(),

    @Column
    var lucesManuales: Boolean = false,

    ) {
    var inicioTrabajoFiltro: LocalDateTime = LocalDateTime.now()
    var finTrabajoFiltro: LocalDateTime = LocalDateTime.now()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    val ultimaActividad: LocalDateTime? = null

    fun activarFuncionFiltro(funcion: FuncionFiltroType) {
        if (entradaAguaActiva.isNotEmpty()) {
            funcionFiltroActivo = funcion
            if (funcion == FuncionFiltroType.FILTRAR || funcion == FuncionFiltroType.RECIRCULAR) {
                inicioTrabajoFiltro = LocalDateTime.now()
                activarSistemasGermicidas()
            }
        }
    }

    fun desactivarFuncionFiltro() {
        if (funcionFiltroActivo == FuncionFiltroType.FILTRAR || funcionFiltroActivo == FuncionFiltroType.RECIRCULAR) {
            finTrabajoFiltro = LocalDateTime.now()
            desactivarSistemasGermicidas(calcularTiempoUsoGermicidas())
            sistemasGermicida.removeIf { !it.activo || it.condicion == CondicionType.MANTENIMIENTO }
        }
        funcionFiltroActivo = FuncionFiltroType.REPOSO
    }

    fun activarSistemasGermicidas() {
        if (sistemasGermicida.isNotEmpty()) {
            sistemasGermicida.forEach { it.activo = true }
        }
    }

    fun calcularTiempoUsoGermicidas(): Int {
        val minutosUso = Duration.between(inicioTrabajoFiltro, LocalDateTime.now()).toMinutes().toInt()
        return minutosUso
    }

    fun desactivarSistemasGermicidas(tiempoUso: Int) {
        if (sistemasGermicida.isNotEmpty()) {
            sistemasGermicida.forEach { it.descontarVida(tiempoUso) }
        }
    }

    companion object {
        fun estadoInicial(piscina: Piscina): EstadoPiscina {
            return EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = mutableListOf(),
                sistemaGermicidaActivo = emptyList(),
                sistemasGermicida = mutableSetOf(),
                funcionFiltroActivo = FuncionFiltroType.REPOSO,
                calefaccionActiva = false,
                fecha = LocalDateTime.now()
            )
        }
    }
}

