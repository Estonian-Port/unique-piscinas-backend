package com.estonianport.unique.model

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import jakarta.persistence.*

@Entity
class Piscina(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val direccion: String,

    @Column
    val ciudad: String,

    @Column
    val esDesbordante: Boolean,

    @Column
    val largo: Double,

    @Column
    val ancho: Double,

    @Column
    val profundidad: Double,

    @Column
    val volumen: Double,

    @Column
    val volumenTC: Double?,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val bomba: MutableList<Bomba>,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    var filtro: Filtro,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val sistemaGermicida: MutableSet<SistemaGermicida>,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
    @JoinColumn(name = "calefaccion_id")
    var calefaccion: Calefaccion?,

    @Column
    var cloroSalino: Boolean,

    @Column
    var controlAutomaticoPH: Boolean,

    @Column
    var orp: Boolean,

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    val plaqueta: Plaqueta,

    @Column(length = 5000)
    val notas: String?,
) {

    // Se deberia poder crear una pileta sin administrador
    // y que el admin total en este caso leo, la maneje y luego asigne a un administrador
    @ManyToOne(fetch = FetchType.LAZY)
    var administrador: Usuario? = null

    @ElementCollection
    @CollectionTable(
        name = "pileta_entrada_agua",
        joinColumns = [JoinColumn(name = "piscina_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "entrada_agua")
    var entradaAgua: MutableList<EntradaAgua> = mutableListOf()

    @ElementCollection
    @CollectionTable(
        name = "pileta_funcion_activa",
        joinColumns = [JoinColumn(name = "piscina_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "funcion_activa")
    var funcionActiva: MutableList<FuncionFiltro> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("id ASC")
    @JoinColumn(name = "piscina_id")
    val programacionFiltrado: MutableSet<ProgramacionFiltrado> = mutableSetOf()

    @Column
    val lucesManual: Boolean = false

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("id ASC")
    @JoinColumn(name = "piscina_id")
    val programacionIluminacion: MutableSet<ProgramacionIluminacion> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "piscina", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lecturas: MutableSet<Lectura> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "piscina", cascade = [CascadeType.ALL], orphanRemoval = true)
    val estados: MutableList<EstadoPiscina> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val erroresLectura: MutableSet<ErrorLectura> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "piscina_id")
    val registros: MutableSet<Registro> = mutableSetOf()

    fun climaLocal() {
        // Implementación de la función clima usando el sensor de la piscina
    }

    fun agregarProgramacionFiltrado(programacion: ProgramacionFiltrado) {
        programacionFiltrado.add(programacion)
    }

    fun agregarProgramacionIluminacion(programacion: ProgramacionIluminacion) {
        programacionIluminacion.add(programacion)
    }

    fun eliminarProgramacionFiltrado(programacionId: Long) {
        programacionFiltrado.remove(programacionFiltrado.find { it.id == programacionId })
    }

    fun eliminarProgramacionLuces(programacionId: Long) {
        programacionIluminacion.remove(programacionIluminacion.find { it.id == programacionId })
    }

    fun agregarRegistro(registro: Registro) {
        registros.add(registro)
    }

    fun eliminarRegistro(registro: Registro) {
        registros.remove(registro)
    }

    fun realizarLectura(lectura: Lectura) {
        // Implementación de la función para realizar una lectura de la piscina mediante la placa de control.
        lecturas.add(lectura)
    }

    @Transient
    fun todasLasLecturas(): List<LecturaBase> {
        return (lecturas + erroresLectura).sortedBy { it.fecha }
    }

}