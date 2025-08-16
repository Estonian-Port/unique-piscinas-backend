package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.UsuarioRequestDto
import com.estonianport.unique.dto.response.UsuarioRegistradoResponseDto
import com.estonianport.unique.dto.response.UsuarioResponseDto
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Usuario
import java.time.LocalDateTime

object UsuarioMapper {

    fun buildUsuarioResponseDto(usuario: Usuario, listaPiscinasId : List<Long>): UsuarioResponseDto {
        return UsuarioResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            email = usuario.email,
            isAdmin = usuario.esAdministrador,
            piscinasId = listaPiscinasId
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

    fun buildUsuario(usuarioDto: UsuarioRequestDto) : Usuario {
        return Usuario (
            id = usuarioDto.id,
            nombre = usuarioDto.nombre,
            apellido = usuarioDto.apellido,
            celular = usuarioDto.celular,
            email = usuarioDto.email
        )
    }

}