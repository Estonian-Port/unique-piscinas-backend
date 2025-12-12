package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.FuncionFiltroRequestDto
import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.mapper.BombaMapper
import com.estonianport.unique.mapper.UsuarioMapper
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.service.AdministracionService
import com.estonianport.unique.service.EstadoPiscinaService
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.PlaquetaService
import com.estonianport.unique.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/estado-piscina")
@CrossOrigin("*")
class EstadoPiscinaController {

    @Autowired
    lateinit var piscinaService: PiscinaService

    @Autowired
    lateinit var estadoPiscinaService: EstadoPiscinaService

    @PostMapping("/encender-luces-manuales/{piscinaId}")
    fun encenderLucesManuales(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        estadoPiscinaService.encenderLuces(piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Luces encendidas manualmente",
                data = null
            )
        )
    }

    @PostMapping("/apagar-luces-manuales/{piscinaId}")
    fun apagarLucesManuales(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        estadoPiscinaService.apagarLuces(piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Luces apagadas manualmente",
                data = null
            )
        )
    }

    @PutMapping("/update-entrada-agua/{piscinaId}")
    fun updateEntradaAgua(
        @PathVariable piscinaId: Long,
        @RequestBody entradaAgua: List<String>
    ): ResponseEntity<CustomResponse> {
        val nuevasEntradas = entradaAgua.map { EntradaAguaType.valueOf(it.uppercase()) }.toMutableList()
        estadoPiscinaService.updateEntradaAgua(piscinaId, nuevasEntradas)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Entrada de agua actualizada correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaService.findById(piscinaId),
                    estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
                )
            )
        )
    }

    @PutMapping("/update-funcion-filtro/{piscinaId}")
    fun updateFuncionActiva(
        @PathVariable piscinaId: Long,
        @RequestBody funcionActiva: FuncionFiltroRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevaFuncionActiva = FuncionFiltroType.valueOf(funcionActiva.funcion.uppercase())
        val patentePlaqueta = piscinaService.getPatentePlaqueta(piscinaId)
        if (nuevaFuncionActiva == FuncionFiltroType.REPOSO) {
            estadoPiscinaService.desactivarFuncionActiva(piscinaId, patentePlaqueta)
            return ResponseEntity.status(200).body(
                CustomResponse(
                    message = "Función activa desactivada correctamente",
                    data = null
                )
            )
        }

        estadoPiscinaService.updateFuncionActiva(piscinaId, nuevaFuncionActiva, patentePlaqueta)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Función activa actualizada correctamente",
                data = null
            )
        )
    }

    data class ActivaRequest(val activa: Boolean)

    @PostMapping("/actualizar-estado-bomba-extra/{piscinaId}/{bombaId}")
    fun actualizarEstadoBombaExtra(
        @PathVariable piscinaId: Long,
        @PathVariable bombaId: Long,
        @RequestBody request: ActivaRequest
    ): ResponseEntity<CustomResponse> {
        val piscinaActualizada = estadoPiscinaService.actualizarEstadoBombaExtra(piscinaId, bombaId, request.activa)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Estado de la bomba extra actualizado correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaActualizada,
                    estadoPiscina = estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
                )
            )
        )
    }

}