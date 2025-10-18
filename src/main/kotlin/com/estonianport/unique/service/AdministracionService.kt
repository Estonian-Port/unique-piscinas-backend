package com.estonianport.unique.service

import com.estonianport.unique.common.codeGeneratorUtil.CodeGeneratorUtil
import com.estonianport.unique.common.errors.IllegalAccessException
import com.estonianport.unique.dto.response.EstadisticasResponseDto
import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.dto.request.PlaquetaRequestDto
import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.model.enums.RolType
import com.estonianport.unique.repository.PlaquetaRepository
import org.springframework.stereotype.Service

@Service
class AdministracionService(
    private val piscinaService: PiscinaService,
    private val usuarioService: UsuarioService,
    private val plaquetaRepository: PlaquetaRepository,
) {

    fun verificarRolAdmin(usuarioId: Long) {
        val usuario = usuarioService.findById(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")

        if (usuario.rol != RolType.ADMIN) {
            throw IllegalAccessException("El usuario no tiene permisos de administrador")
        }
    }

    fun getEstadisticas(): EstadisticasResponseDto {
        return EstadisticasResponseDto(
            totalPiscinas = piscinaService.totalPiscinas(),
            totalUsuarios = usuarioService.totalUsuarios(),
            usuariosActivos = usuarioService.countUsuariosActivos(),
            usuariosPendientes = usuarioService.countUsuariosPendientes(),
            usuariosInactivos = usuarioService.countUsuariosInactivos(),
            piscinasSkimmer = piscinaService.countPiscinasDesborde(),
            piscinasDesborde = piscinaService.countPiscinasSkimmer(),
            volumenTotal = piscinaService.getVolumenTotal(),
            volumenPromedio = piscinaService.getVolumenPromedio(),
        )
    }

    fun verificarRolPatGen(usuarioId: Long) {
        val usuario = usuarioService.findById(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")

        if (usuario.rol != RolType.PAT_GEN) {
            throw IllegalAccessException("El usuario no tiene permisos de administrador")
        }
    }

    fun generarNuevaPatente(plaquetaRequestDto: PlaquetaRequestDto): String {
        return plaquetaRepository.save(
            Plaqueta(
                patente = CodeGeneratorUtil.base26Only4Letters,
                firmware =plaquetaRequestDto.firmware,
                tipo=plaquetaRequestDto.tipo )
        ).patente
    }

}