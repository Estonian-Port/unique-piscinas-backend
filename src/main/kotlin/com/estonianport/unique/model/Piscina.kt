package com.estonianport.unique.model

import com.estonianport.unique.common.errors.BusinessException
import com.estonianport.unique.model.enums.EntradaAgua
import com.estonianport.unique.model.enums.FuncionFiltro
import jakarta.persistence.*

@Entity
class Piscina(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val nombre: String,

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

    // Solo se usa para piscinas de tipo desbordante.
    @Column
    val volumenTC: Double?,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val bomba: MutableList<Bomba>,

    // Filtro entiendo que es uno solo pero habria que confirmar con Leo.
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @PrimaryKeyJoinColumn
    val filtro: Filtro,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val valvulas: MutableSet<Valvula>,

    // Dispositivos de la piscina: Son opcionales y corresponden a UV, Ionizador y Trasductor.
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val sistemaGermicida: MutableSet<SistemaGermicida>,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @PrimaryKeyJoinColumn
    val calefaccion: Calefaccion?,

    // Los siguiente atributos en el prototipo aparecen en el formulario de añadir piscina pero luego esa informacion no se muestra en ningun lado.
    // Consultar a Leo si quiere mostrarla, por ejemplo junto con los dispostivos, o si los sacamos.
    @Column
    val cloroSalino: Boolean,

    @Column
    val controlAutomaticoPH: Boolean,

    @Column
    val orp: Boolean,
) {

    // Lo puse aca porque entiendo que se deberia poder crear una pileta sin administrador
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
    val entradaAgua: MutableList<EntradaAgua> = mutableListOf()

    @ElementCollection
    @CollectionTable(
        name = "pileta_funcion_activa",
        joinColumns = [JoinColumn(name = "piscina_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "funcion_activa")
    val funcionActiva: MutableList<FuncionFiltro> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val programacionFiltrado: MutableSet<Programacion> = mutableSetOf()

    @Column
    val lucesManual: Boolean = false

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val programacionLuces: MutableSet<Programacion> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val lecturas: MutableSet<Lectura> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val erroresLectura: MutableSet<ErrorLectura> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id")
    val registros: MutableSet<Registro> = mutableSetOf()

    fun climaLocal() {
        // Implementación de la función clima pegandole a una API externa
    }

    fun agregarProgramacionFiltrado(programacion: Programacion) {
        programacionFiltrado.add(programacion)
    }

    fun agregarProgramacionLuces(programacion: Programacion) {
        programacionLuces.add(programacion)
    }

    fun eliminarProgramacionFiltrado(programacion: Programacion) {
        programacionFiltrado.remove(programacion)
    }

    fun eliminarProgramacionLuces(programacion: Programacion) {
        programacionLuces.remove(programacion)
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

    fun tieneCalefaccion() = calefaccion != null

    fun filtroActivo() : Boolean = (entradaAgua.isNotEmpty() and funcionActiva.isNotEmpty())
}