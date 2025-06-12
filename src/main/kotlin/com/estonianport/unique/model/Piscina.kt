package com.estonianport.unique.model

class Piscina (
    val nombre: String,
    val administrador: Usuario,
    val direccion: String,
    val ciudad: String,
    val esDesbordante: Boolean,
    val largo: Double,
    val ancho: Double,
    val profundidad: Double,
    val volumen: Double,
    val volumenTC: Double?,
    val bomba: MutableList<Bomba>,
    val filtro: Filtro,
    val cloroSalino: Boolean,
    val controlAutomaticoPH: Boolean,
    val orp: Boolean,
    val uv: Boolean,
    val ionizador: Boolean,
    val trasductor: Boolean,
    val calefaccion: Boolean,
    val luces: Boolean, //PREGUNTAR SI TODAS LAS PISCINAS TIENEN LUCES
){
    val presion: Double = 0.0
    val programacionFiltrado: MutableList<Programacion> = mutableListOf()
    val programacionLuces: MutableList<Programacion> = mutableListOf()

    fun añadirProgramacionFiltrado(programacion: Programacion) {
        programacionFiltrado.add(programacion)
    }

    fun añadirProgramacionLuces(programacion: Programacion) {
        programacionLuces.add(programacion)
    }

    fun eliminarProgramacionFiltrado(programacion: Programacion) {
        programacionFiltrado.remove(programacion)
    }

    fun eliminarProgramacionLuces(programacion: Programacion) {
        programacionLuces.remove(programacion)
    }
}

interface Bomba {
    val potencia: Double
    val tipo: String
}

interface Filtro {
    val tipo: String
}

interface Programacion {
    val horaInicio: String
    val horaFin: String
    val dias: List<String>
    val estaActivos: Boolean
}