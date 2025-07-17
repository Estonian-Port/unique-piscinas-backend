package com.estonianport.unique.mapper

import com.estonianport.unique.dto.response.UsuarioRegistradoResponseDto
import com.estonianport.unique.dto.response.UsuarioResponseDto
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Usuario

object UsuarioMapper {

    fun buildUsuarioResponseDto(usuario: Usuario, piscinasAsignadas : Int): UsuarioResponseDto {
        return UsuarioResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            email = usuario.email,
            isAdmin = usuario.esAdministrador,
            cantidadPiscinas = piscinasAsignadas
        )
    }

    fun buildUsuarioRegistradoResponseDto(usuario: Usuario, piscinasAsignadas: List<Piscina>): UsuarioRegistradoResponseDto {
        return UsuarioRegistradoResponseDto(
            id = usuario.id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            email = usuario.email,
            piscinasAsignadas = piscinasAsignadas.map { PiscinaMapper.buildPiscinaAsignadaResponseDto(it) },
        )
    }

}