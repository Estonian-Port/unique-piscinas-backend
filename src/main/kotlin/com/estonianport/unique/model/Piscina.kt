package com.estonianport.unique.model

import com.estonianport.unique.model.enums.BombaType
import com.estonianport.unique.model.enums.FuncionFiltroType
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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plaqueta_id", nullable = false, unique = true)
    val plaqueta: Plaqueta,

    @Column(length = 5000)
    val notas: String?,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "piscina", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id ASC")
    val programaciones: MutableSet<Programacion> = mutableSetOf(),
) {

    // Se deberia poder crear una piscina sin administrador
    // y que el admin total en este caso leo, la maneje y luego asigne a un administrador
    @ManyToOne(fetch = FetchType.LAZY)
    var administrador: Usuario? = null

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

    fun agregarProgramacionFiltrado(programacion: Programacion) {
        programacion.piscina = this
        programaciones.add(programacion)
    }

    fun agregarProgramacionIluminacion(programacion: Programacion) {
        programacion.piscina = this
        programaciones.add(programacion)
    }

    fun eliminarProgramacionFiltrado(programacionId: Long) {
        programaciones.removeIf { it.id == programacionId }
    }

    fun eliminarProgramacionIluminacion(programacionId: Long) {
        programaciones.removeIf { it.id == programacionId }
    }

    fun agregarRegistro(registro: Registro) {
        registros.add(registro)
    }

    fun eliminarRegistro(registro: Registro) {
        registros.remove(registro)
    }

    fun agregarNuevoEstadoPiscina(estadoPiscina: EstadoPiscina) {
        estados.add(estadoPiscina)
    }

    fun realizarLectura(lectura: Lectura) {
        // Implementación de la función para realizar una lectura de la piscina mediante la placa de control.
        lecturas.add(lectura)
    }

    fun desactivarSistemasGermicidas(tiempoUso: Int) {
        if (sistemaGermicida.isNotEmpty()) {
            sistemaGermicida.forEach { it.descontarVida(tiempoUso) }
        }
    }

    fun activarSistemasGermicidas() {
        if (sistemaGermicida.isNotEmpty()) {
            sistemaGermicida.forEach { it.activo = true }
        }
    }

    fun verificarEstados(piscina: Piscina) {
        val estadoActual = estados.maxByOrNull { it.fecha } ?: return

        if (estadoActual.funcionFiltroActivo != FuncionFiltroType.REPOSO) {
            filtro.activo = true
            piscina.actualizarEstadoBombaPrincipal(true)
        } else {
            filtro.activo = false
            piscina.actualizarEstadoBombaPrincipal(false)
        }
    }

    fun estadoActual(): EstadoPiscina? {
        return estados.maxByOrNull { it.fecha }
    }

    fun actualizarEstadoBombaPrincipal(activa: Boolean) {
        val bombaPrincipal = bomba.find { it.tipo == BombaType.PRINCIPAL }
        bombaPrincipal?.activa = activa
    }

    @Transient
    fun todasLasLecturas(): List<LecturaBase> {
        return (lecturas + erroresLectura).sortedBy { it.fecha }
    }

}