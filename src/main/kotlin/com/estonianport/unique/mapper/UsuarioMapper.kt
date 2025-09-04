package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.UsuarioAltaRequestDto
import com.estonianport.unique.dto.request.UsuarioRequestDto
import com.estonianport.unique.dto.response.UsuarioNuevaPiscinaResponseDto
import com.estonianport.unique.dto.response.UsuarioRegistradoResponseDto
import com.estonianport.unique.dto.response.UsuarioResponseDto
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Usuario
import com.estonianport.unique.model.enums.UsuarioType
import java.time.LocalDateTime

object UsuarioMapper {

    fun buildUsuarioResponseDto(usuario: Usuario, listaPiscinasId : List<Long>): UsuarioResponseDto {
        return UsuarioResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            email = usuario.email,
            isAdmin = usuario.esAdministrador,
            piscinasId = listaPiscinasId,
            primerLogin = usuario.estado == UsuarioType.PENDIENTE
        )
    }

    fun buildUsuarioRegistradoResponseDto(usuario: Usuario, piscinasAsignadas: List<Piscina>): UsuarioRegistradoResponseDto {
        return UsuarioRegistradoResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            celular = usuario.celular,
            email = usuario.email,
            estado = usuario.estado.name,
            piscinasAsignadas = piscinasAsignadas.map { PiscinaMapper.buildPiscinaAsignadaResponseDto(it) },
        )
    }

    fun buildUsuarioNuevaPiscinaResponseDto(usuario: Usuario): UsuarioNuevaPiscinaResponseDto {
        return UsuarioNuevaPiscinaResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
        )
    }

    fun buildUsuario(usuarioDto: UsuarioRequestDto) : Usuario {
        return Usuario (
            id = usuarioDto.id,
            nombre = usuarioDto.nombre,
            apellido = usuarioDto.apellido,
            celular = usuarioDto.celular,
            email = usuarioDto.email
        )
    }

    fun buildAltaUsuario(usuarioDto: UsuarioAltaRequestDto) : Usuario {
        return Usuario (
            id = 0,
            nombre = "",
            apellido = "",
            celular = 0,
            email = usuarioDto.email
        )
    }

}