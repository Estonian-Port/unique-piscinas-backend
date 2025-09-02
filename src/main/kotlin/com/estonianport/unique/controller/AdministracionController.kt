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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/administracion")
@CrossOrigin("*")
class AdministracionController {

    @Autowired
    private lateinit var piscinaService: PiscinaService

    @Autowired
    lateinit var administracionService: AdministracionService

    @Autowired
    lateinit var usuarioService: UsuarioService

    // Tengo la duda si todos los endpoints de administracion deberian chequear si el usuario es administrador.
    // Por el momento lo hago para todos.

    @GetMapping("/estadisticas/{usuarioId}")
    fun getEstadisticas(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Estadísticas obtenidas correctamente",
                data = administracionService.getEstadisticas()
            )
        )
    }

    @GetMapping("/piscinas-registradas/{usuarioId}")
    fun getPiscinasRegistradas(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas registradas obtenidas correctamente",
                data = piscinaService.getPiscinasRegistradas()
                    .map { PiscinaMapper.buildPiscinaRegistradaListDto(it, piscinaService.getPh(it.id)) }
            )
        )
    }

    @GetMapping("/piscina-ficha-tecnica/{usuarioId}/{piscinaId}")
    fun getPiscinaFichaTecnicaById(@PathVariable piscinaId: Long, @PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Ficha técnica de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaFichaTecnicaDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @GetMapping("/piscina-equipos/{usuarioId}/{piscinaId}")
    fun getPiscinaEquipoById(@PathVariable piscinaId: Long, @PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Equipos de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaEquiposResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @GetMapping("/usuarios-registrados/{usuarioId}")
    fun getUsuarioRegistrados(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuarios registrados obtenidas correctamente",
                data = usuarioService.getUsuariosRegistrados().map {
                    UsuarioMapper.buildUsuarioRegistradoResponseDto(
                        it,
                        piscinaService.getPiscinasByUsuarioId(it.id)
                    )
                }
            )
        )
    }

    @GetMapping("/no-asignadas")
    fun getPiscinasNoAsignadas(): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas sin asignar obtenidas correctamente",
                data = piscinaService.getPiscinasSinAdministrador()
                    .map { PiscinaMapper.buildPiscinaListResponseDto(it) }
            )
        )
    }

    @PutMapping("/desasginar-piscina/{piscinaId}/{usuarioId}/{administradorId}")
    fun desasignarAdministrador(
        @PathVariable piscinaId: Long,
        @PathVariable usuarioId: Long,
        @PathVariable administradorId: Long
    ): ResponseEntity<CustomResponse> {
        administracionService.verificarRol(administradorId)
        piscinaService.desasignarAdministrador(usuarioId, piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuario desasignado correctamente",
                data = PiscinaMapper.buildPiscinaListResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

}