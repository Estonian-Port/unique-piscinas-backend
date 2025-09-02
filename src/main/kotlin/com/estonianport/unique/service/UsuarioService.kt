package com.estonianport.unique.service

import GenericServiceImpl
import com.estonianport.unique.common.codeGeneratorUtil.CodeGeneratorUtil
import com.estonianport.unique.dto.UsuarioAbmDTO
import com.estonianport.unique.dto.UsuarioPerfilDTO
import com.estonianport.unique.dto.request.UsuarioRequestDto
import com.estonianport.unique.repository.UsuarioRepository
import com.estonianport.unique.model.Usuario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.CrudRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService : GenericServiceImpl<Usuario, Long>() {

    @Autowired
    lateinit var usuarioRepository: UsuarioRepository

    override val dao: CrudRepository<Usuario, Long>
        get() = usuarioRepository

/*
    fun getAllUsuario(id : Long, pageNumber : Int): List<UsuarioAbmDTO> {
        return usuarioRepository.getAllUsuario(id, PageRequest.of(pageNumber,10)).content
    }

    fun getAllUsuarioFiltrados(id : Long, pageNumber : Int, buscar: String): List<UsuarioAbmDTO>{
        return usuarioRepository.getAllUsuarioFiltrados(id, buscar, PageRequest.of(pageNumber,10)).content
    }

    fun getCantidadUsuario(id : Long): Int {
        return usuarioRepository.getCantidadUsuario(id)
    }

    fun getCantidadUsuarioFiltrados(id : Long, buscar : String): Int {
        return usuarioRepository.getCantidadUsuarioFiltrados(id,buscar)
    }

*/
    fun getUsuarioByEmail(email: String): Usuario {
        return usuarioRepository.getUsuarioByEmail(email)
            ?: throw NoSuchElementException("No se encontró un usuario con el email proporcionado")
    }

    fun getUsuarioByCelular(celular : Long): Usuario?{
        return usuarioRepository.getUsuarioByCelular(celular)
    }

    fun findById(id : Long) : Usuario? {
        return usuarioRepository.findById(id).get()
    }

    fun totalUsuarios() : Int {
        return usuarioRepository.totalUsuarios()
    }

    fun getUsuariosActivos() : Int {
        return usuarioRepository.countUsuariosActivos()
    }

    fun getUsuariosInactivos() : Int {
        return usuarioRepository.countUsuariosInactivos()
    }

    fun getUsuariosPendientes() : Int {
        return usuarioRepository.countUsuariosPendientes()
    }

    fun getUsuariosRegistrados(): List<Usuario> {
        return getAll()!!.filter { !it.esAdministrador }
    }

     fun encriptarPassword(password: String) : String {
        return BCryptPasswordEncoder().encode(password)
    }

    fun generarPassword(): String {
        return CodeGeneratorUtil.base26Only4Letters + CodeGeneratorUtil.base26Only4Letters
    }

}
