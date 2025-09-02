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
            totalUsuarios = usuarioService.totalUsuarios(),
            usuariosActivos = usuarioService.getUsuariosActivos(),
            usuariosPendientes = usuarioService.getUsuariosPendientes(),
            usuariosInactivos = usuarioService.getUsuariosInactivos(),
            piscinasSkimmer = piscinaService.countPiscinasDesborde(),
            piscinasDesborde = piscinaService.countPiscinasSkimmer(),
            volumenTotal = piscinaService.getVolumenTotal(),
            volumenPromedio = piscinaService.getVolumenPromedio(),
        )
    }

}