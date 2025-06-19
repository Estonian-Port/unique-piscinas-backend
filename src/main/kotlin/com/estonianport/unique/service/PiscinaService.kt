package com.estonianport.unique.service

import com.estonianport.unique.model.Piscina
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service

@Service
class PiscinaService(private val piscinaRepository: PiscinaRepository, private val usuarioService: UsuarioService) {

    fun getPiscinasByUsuarioId(usuarioId: String): List<Piscina> {
        usuarioService.findById(usuarioId.toLong())
            ?: throw IllegalArgumentException("Usuario no encontrado con ID: $usuarioId")
        return piscinaRepository.findByUsuarioId(usuarioId.toLong())
    }

    fun findById(piscinaId: String): Piscina {
        return piscinaRepository.findById(piscinaId.toLong())
            ?: throw IllegalArgumentException("Piscina no encontrada con ID: $piscinaId")
    }

}