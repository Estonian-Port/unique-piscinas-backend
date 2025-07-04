package com.estonianport.unique.service

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Programacion
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service

@Service
class PiscinaService(private val piscinaRepository: PiscinaRepository, private val usuarioService: UsuarioService) {

    fun getPiscinasByUsuarioId(usuarioId: Long): List<Piscina> {
        usuarioService.findById(usuarioId)
            ?: throw IllegalArgumentException("Usuario no encontrado con ID: $usuarioId")
        return piscinaRepository.findByAdministradorId(usuarioId)
    }

    fun findById(piscinaId: Long): Piscina {
        return piscinaRepository.findById(piscinaId)
            ?: throw IllegalArgumentException("Piscina no encontrada con ID: $piscinaId")
    }

    fun getLecturasPiscina(piscinaId: Long): List<LecturaConErrorResponseDto> {
        return piscinaRepository.findTodasLecturasConError(piscinaId)
    }

    fun getPresion(piscinaId: Long): Double {
        return piscinaRepository.getPresion(piscinaId)
            ?: throw IllegalArgumentException("Presion de piscina: $piscinaId no encontrado")
    }

    fun getPh(piscinaId: Long): Double {
        return piscinaRepository.getPh(piscinaId)
            ?: throw IllegalArgumentException("Ph de piscina: $piscinaId no encontrado")
    }

    fun getDiferenciaPh(piscinaId: Long): Double {
        val ultimasDosPh = piscinaRepository.getDiferenciaPh(piscinaId)
            ?: throw IllegalArgumentException("Ph de piscina: $piscinaId no encontrado")
        return ultimasDosPh[0] - ultimasDosPh[1]
    }

    fun totalPiscinas(): Int {
        return piscinaRepository.count().toInt()
    }

    fun countPiscinasByTipo(tipo: String): Int {
        return piscinaRepository.countByTipo(tipo)
    }

    fun getVolumenTotal(): Double {
        return piscinaRepository.getTotalVolumen() ?: 0.0
    }

    fun getVolumenPromedio(): Double {
        return piscinaRepository.getPromedioVolumen() ?: 0.0
    }

    fun getPiscinasRegistradas(): List<Piscina> {
        return piscinaRepository.findAll()
    }

    fun getPiscinasSinAdministrador(): List<Piscina> {
        return piscinaRepository.findByAdministradorIsNull()
    }

    fun desasignarAdministrador(usuarioId: Long, piscinaId: Long) {
        val usuario = usuarioService.findById(usuarioId)
            ?: throw IllegalArgumentException("Usuario no encontrado con ID: $usuarioId")
        val piscina = findById(piscinaId)
        //piscina.administrador.remove(usuario)
        // Creo que deberiamos tener una lista de administradores en vez de uno solo. Porque Leo debe tener permisos
        // y tambien el usuario que Leo asigne. Si no hay que crear dos entradas en la tabla de piscina. Una para adminitrador
        // y otra para usuario a cargo de la piscina.
        piscina.administrador = null
        piscinaRepository.save(piscina)
    }

    fun deleteProgramacion(piscinaId: Long, programacionId: Long) {
        val piscina = findById(piscinaId)
        piscina.programacionLuces.removeIf { it.id == programacionId }
        piscina.programacionFiltrado.removeIf { it.id == programacionId }
        piscinaRepository.save(piscina)
    }

    fun agregarProgramacion(piscinaId: Long, programacion: Programacion, filtrado: Boolean) {
        val piscina = findById(piscinaId)
        if (filtrado) piscina.agregarProgramacionFiltrado(programacion)
        if (!filtrado) piscina.agregarProgramacionLuces(programacion)
        piscinaRepository.save(piscina)
    }

    fun updateProgramacion(
        piscinaId: Long,
        programacion: Programacion,
        filtrado: Boolean
    ) {
        val piscina = findById(piscinaId)
        val lista = if (filtrado) piscina.programacionFiltrado else piscina.programacionLuces
        actualizarListaProgramacion(lista.toList(), programacion)
        piscinaRepository.save(piscina)
    }

    private fun actualizarListaProgramacion(
        lista: List<Programacion>,
        programacion: Programacion
    ) {
        lista.find { it.id == programacion.id }?.apply {
            horaInicio = programacion.horaInicio
            horaFin = programacion.horaFin
            dias = programacion.dias
            estaActivo = programacion.estaActivo
        }
    }
}