package com.estonianport.unique.service

import com.estonianport.unique.common.errors.IllegalAccessException
import com.estonianport.unique.dto.response.EstadisticasResponseDto
import com.estonianport.unique.common.errors.NotFoundException
import org.springframework.stereotype.Service

@Service
class AdministracionService(
    private val piscinaService: PiscinaService,
    private val usuarioService: UsuarioService,
) {

    fun verificarRol(usuarioId: Long) {
        val usuario = usuarioService.findById(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")

        if (!usuario.esAdministrador) {
            throw IllegalAccessException("El usuario no tiene permisos de administrador")
        }
    }

    fun getEstadisticas(): EstadisticasResponseDto {
        return EstadisticasResponseDto(
            totalPiscinas = piscinaService.totalPiscinas(),
            totalUsuarios = usuarioService.count().toInt(),
            usuariosActivos = usuarioService.getUsuariosActivos(),
            usuariosInactivos = usuarioService.count().toInt() - usuarioService.getUsuariosActivos(),
            piscinasSkimmer = piscinaService.countPiscinasByTipo("Skimmer"),
            piscinasDesborde = piscinaService.countPiscinasByTipo("Desborde"),
            volumenTotal = piscinaService.getVolumenTotal(),
            volumenPromedio = piscinaService.getVolumenPromedio(),
        )
    }

}