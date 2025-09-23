package com.estonianport.unique.model

import com.estonianport.unique.model.enums.*
import jakarta.persistence.*
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

    @Column
    val calefaccionActiva: Boolean,

    @Column(nullable = false)
    val fecha: LocalDateTime = LocalDateTime.now()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    companion object {
        fun estadoInicial(piscina: Piscina): EstadoPiscina {
            return EstadoPiscina(
                piscina = piscina,
                entradaAguaActiva = mutableListOf(),
                sistemaGermicidaActivo = emptyList(),
                funcionFiltroActivo = FuncionFiltroType.REPOSO,
                calefaccionActiva = false,
                fecha = LocalDateTime.now()
            )
        }
    }
}

