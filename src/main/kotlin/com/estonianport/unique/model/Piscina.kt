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
    val administrador: Usuario,

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

    // Puede tener una o dos bombas.
    val bomba: MutableList<Bomba>,

    // Filtro entiendo que es uno solo pero habria que confirmar con Leo.
    @Column
    val filtro: Filtro,

    val valvulas: MutableSet<Valvula>,

    // Dispositivos de la piscina: Son opcionales y corresponden a UV, Ionizador y Trasductor.
    val sistemaGermicida: MutableSet<SistemaGermicida>,

    @Column
    val calefaccion: Calefaccion? = null,

    // Los siguiente atributos en el prototipo aparecen en el formulario de añadir piscina pero luego esa informacion no se muestra en ningun lado.
    // Consultar a Leo si quiere mostrarla, por ejemplo junto con los dispostivos, o si los sacamos.
    @Column
    val cloroSalino: Boolean,

    @Column
    val controlAutomaticoPH: Boolean,

    @Column
    val orp: Boolean,
) {
    @Column
    val entradaAgua: MutableList<EntradaAgua> = mutableListOf()

    @Column
    val funcionActiva: MutableList<FuncionFiltro> = mutableListOf()

    @Column
    val programacionFiltrado: MutableSet<Programacion> = mutableSetOf()

    @Column
    val lucesManual: Boolean = false

    @Column
    val programacionLuces: MutableSet<Programacion> = mutableSetOf()

    @Column
    val lecturas: MutableSet<Lectura> = mutableSetOf()

    @Column
    val registros: MutableSet<Registro> = mutableSetOf()


    fun climaLocal() {
        // Implementación de la función clima pegandole a una API externa
    }

    fun ph(): Double {
        // Implementación de la función para obtener el pH de la piscina. Dato que nos dara la placa de control.
        // Pienso que se puede buscar la ultima lectura de pH en la lista de lecturas y retornar ese valor.
        if (lecturas.isEmpty()) {
            throw BusinessException("No hay lecturas de pH disponibles.")
        }
        return lecturas.last().ph
    }

    fun presion(): Double {
        // Implementación de la función para obtener la presion de la piscina. Dato que nos dara la placa de control.
        // Pienso que se puede buscar la ultima lectura de presion en la lista de lecturas y retornar ese valor.
        if (lecturas.isEmpty()) {
            throw BusinessException("No hay lecturas de presion disponibles.")
        }
        return lecturas.last().presion
    }

    fun diferenciaPh(): Double {
        if (lecturas.size > 1) {
            val penultimaLectura = lecturas.elementAt(lecturas.size - 2)
            return ph() - penultimaLectura.ph
        }
        return 0.0
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
}