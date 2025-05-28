package com.estonianport.unique.service

import GenericServiceImpl
import com.estonianport.unique.dto.UsuarioAbmDTO
import com.estonianport.unique.dto.UsuarioPerfilDTO
import com.estonianport.unique.repository.UsuarioRepository
import com.estonianport.unique.model.Usuario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService : GenericServiceImpl<Usuario, Long>() {

    @Autowired
    lateinit var usuarioRepository: UsuarioRepository

    override val dao: CrudRepository<Usuario, Long>
        get() = usuarioRepository

    fun getUsuarioIdByUsername(username: String): Long {
        return usuarioRepository.getByUsername(username).id
    }
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

    fun getAllCliente(id : Long, pageNumber : Int): List<UsuarioAbmDTO> {
        return usuarioRepository.getAllCliente(id, PageRequest.of(pageNumber,10)).content
    }

    fun getAllClienteFiltrados(id : Long, pageNumber : Int, buscar: String): List<UsuarioAbmDTO>{
        return usuarioRepository.getAllClienteFiltrados(id, buscar, PageRequest.of(pageNumber,10)).content
    }

    fun getCantidadCliente(id : Long): Int {
        return usuarioRepository.getCantidadCliente(id)
    }

    fun getCantidadClienteFiltrados(id : Long, buscar : String): Int {
        return usuarioRepository.getCantidadClienteFiltrados(id,buscar)
    }
*/
    fun getUsuarioByEmail(email : String) : Usuario?{
        return usuarioRepository.getUsuarioByEmail(email)
    }

    fun getUsuarioByCelular(celular : Long): Usuario?{
        return usuarioRepository.getUsuarioByCelular(celular)
    }

    fun findById(id : Long) : Usuario? {
        return usuarioRepository.findById(id).get()
    }
/*
    fun getUsuarioPerfil(usuarioId: Long): UsuarioPerfilDTO {
        return usuarioRepository.getUsuarioPerfil(usuarioId)
    }
    */

}
