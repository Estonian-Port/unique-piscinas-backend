package com.estonianport.unique.service

import com.estonianport.unique.dto.response.EstadisticasResponseDto
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service

@Service
class AdministracionService(private val piscinaRepository: PiscinaRepository, private val usuarioService: UsuarioService) {

    fun verificarRol(usuarioId: Long) {
        val usuario = usuarioService.findById(usuarioId)
            ?: throw IllegalArgumentException("Usuario no encontrado con ID: $usuarioId")

        if (!usuario.esAdministrador) {
            throw IllegalAccessException("El usuario no tiene permisos de administrador")
        }
    }

    fun getEstadisticas(): EstadisticasResponseDto {
        return EstadisticasResponseDto(
            totalPiscinas = piscinaRepository.count().toInt(),
            totalUsuarios = usuarioService.count().toInt(),
            usuariosActivos = usuarioService.getUsuariosActivos(),
            usuariosInactivos = usuarioService.count().toInt() - usuarioService.getUsuariosActivos(),
            piscinasSkimmer = piscinaRepository.countByTipo("Skimmer"),
            piscinasDesborde = piscinaRepository.countByTipo("Desborde"),
            volumenTotal = piscinaRepository.getTotalVolumen(),
            volumenPromedio = piscinaRepository.getPromedioVolumen()
        )

    }

}