package com.estonianport.unique.service

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.model.Piscina
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

}