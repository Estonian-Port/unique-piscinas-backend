package com.estonianport.unique.controller

import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.mapper.UsuarioMapper
import com.estonianport.unique.service.AdministracionService
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/usuario")
@CrossOrigin("*")
class UsuarioController {

    @Autowired
    lateinit var administracionService: AdministracionService

    @Autowired
    lateinit var usuarioService: UsuarioService

    @DeleteMapping("/{usuarioId}/{administradorId}")
    fun deleteUsuario(
        @PathVariable usuarioId: Long,
        @PathVariable administradorId: Long
    ): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(administradorId)
        usuarioService.delete(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuario eliminado correctamente",
                data = null
            )
        )
    }

}