package com.estonianport.unique.controller

import com.estonianport.unique.common.codeGeneratorUtil.CodeGeneratorUtil
import com.estonianport.unique.dto.request.PlaquetaRequestDto
import com.estonianport.unique.dto.request.UsuarioAltaRequestDto
import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.dto.response.PiscinaFichaTecnicaDto
import com.estonianport.unique.mapper.UsuarioMapper
import com.estonianport.unique.service.AdministracionService
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.PlaquetaService
import com.estonianport.unique.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/administracion")
@CrossOrigin("*")
class AdministracionController {

    @Autowired
    lateinit var piscinaService: PiscinaService

    @Autowired
    lateinit var administracionService: AdministracionService

    @Autowired
    lateinit var usuarioService: UsuarioService

    @Autowired
    lateinit var plaquetaService: PlaquetaService

    @GetMapping("/estadisticas/{usuarioId}")
    fun getEstadisticas(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Estadísticas obtenidas correctamente",
                data = administracionService.getEstadisticas()
            )
        )
    }

    @GetMapping("/piscinas-registradas/{usuarioId}")
    fun getPiscinasRegistradas(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas registradas obtenidas correctamente",
                data = piscinaService.getPiscinasRegistradas()
                    .map { PiscinaMapper.buildPiscinaRegistradaListDto(it, piscinaService.getPh(it.id)) }
            )
        )
    }

    @GetMapping("/piscina-ficha-tecnica/{usuarioId}/{piscinaId}")
    fun getPiscinaFichaTecnicaById(
        @PathVariable piscinaId: Long,
        @PathVariable usuarioId: Long
    ): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Ficha técnica de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaFichaTecnicaDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @GetMapping("/piscina-equipos/{usuarioId}/{piscinaId}")
    fun getPiscinaEquipoById(
        @PathVariable piscinaId: Long,
        @PathVariable usuarioId: Long
    ): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Equipos de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaEquiposResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @GetMapping("/usuarios-nueva-piscina/{usuarioId}")
    fun getUsuarioNuevaPiscina(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuarios para nueva piscina obtenidos correctamente",
                data = usuarioService.getUsuariosRegistrados().map {
                    UsuarioMapper.buildUsuarioNuevaPiscinaResponseDto(it)
                }
            )
        )
    }

    @GetMapping("/patentes-nueva-piscina")
    fun getPatentesNuevaPiscina(): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Patentes para nueva piscina obtenidos correctamente",
                data = plaquetaService.getPatentesDisponibles()
            )
        )
    }

    @GetMapping("/usuarios-registrados/{usuarioId}")
    fun getUsuarioRegistrados(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
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

    @GetMapping("/usuarios-pendientes/{usuarioId}")
    fun getUsuarioPendientes(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        administracionService.verificarRolAdmin(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuarios pendientes obtenidas correctamente",
                data = usuarioService.getUsuariosPendientes().map {
                    UsuarioMapper.buildUsuarioPendienteResponseDto(
                        it
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
                    .map { PiscinaMapper.buildPiscinaHeaderResponseDto(it) }
            )
        )
    }

    @PutMapping("/asignar-piscina/{piscinaId}/{usuarioId}")
    fun asignarPiscina(
        @PathVariable piscinaId: Long,
        @PathVariable usuarioId: Long
    ): ResponseEntity<CustomResponse> {
        piscinaService.asignarAdministrador(usuarioId, piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscina asignada correctamente",
                data = UsuarioMapper.buildUsuarioRegistradoResponseDto(usuarioService.findById(usuarioId)!!, piscinaService.getPiscinasByUsuarioId(usuarioId))
            )
        )
    }

    @PutMapping("/desvincular-piscina/{piscinaId}/{usuarioId}")
    fun devincularAdministrador(
        @PathVariable piscinaId: Long,
        @PathVariable usuarioId: Long,
    ): ResponseEntity<CustomResponse> {
        piscinaService.desvincularAdministrador(usuarioId, piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Usuario desvinculado correctamente",
                data = UsuarioMapper.buildUsuarioRegistradoResponseDto(usuarioService.findById(usuarioId)!!, piscinaService.getPiscinasByUsuarioId(usuarioId))
            )
        )
    }

    @PostMapping("/generar-patente/{usuarioId}")
fun getGenerarPatente(@PathVariable usuarioId: Long, @RequestBody plaquetaRequestDto : PlaquetaRequestDto
    ): ResponseEntity<CustomResponse> {
        administracionService.verificarRolPatGen(usuarioId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Nueva patente generada correctamente",
                data = administracionService.generarNuevaPatente(plaquetaRequestDto)
            )
        )
    }

    @DeleteMapping("/eliminar-invitacion")
    fun eliminarInvitacion(@RequestBody usuarioDto: UsuarioAltaRequestDto): ResponseEntity<CustomResponse> {
        usuarioService.eliminarInvitacionPorEmail(usuarioDto.email)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Invitación eliminada correctamente",
                data = null
            )
        )
    }

}