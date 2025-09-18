package com.estonianport.unique.controller

import com.estonianport.unique.dto.request.BombaRequestDto
import com.estonianport.unique.dto.request.CalefaccionRequestDto
import com.estonianport.unique.dto.request.FiltroRequestDto
import com.estonianport.unique.dto.request.PiscinaCompuestosRequestDto
import com.estonianport.unique.dto.request.PiscinaRequestDto
import com.estonianport.unique.dto.request.ProgramacionRequestDto
import com.estonianport.unique.dto.request.SistemaGermicidaRequestDto
import com.estonianport.unique.mapper.PiscinaMapper
import com.estonianport.unique.dto.response.CustomResponse
import com.estonianport.unique.dto.response.RegistroResponseDto
import com.estonianport.unique.mapper.BombaMapper
import com.estonianport.unique.mapper.CalefaccionMapper
import com.estonianport.unique.mapper.FiltroMapper
import com.estonianport.unique.mapper.ProgramacionMapper
import com.estonianport.unique.mapper.RegistroMapper
import com.estonianport.unique.mapper.SistemaGermicidaMapper
import com.estonianport.unique.model.enums.UsuarioType
import com.estonianport.unique.repository.PlaquetaRepository
import com.estonianport.unique.service.PiscinaService
import com.estonianport.unique.service.UsuarioService
import org.hibernate.usertype.UserType
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

@RestController
@RequestMapping("/piscina")
@CrossOrigin("*")
class PiscinaController {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    lateinit var piscinaService: PiscinaService

    @Autowired
    lateinit var plaquetaRepository: PlaquetaRepository

    @GetMapping("/getAll/{usuarioId}")
    fun getAllPiscinasByUsuarioId(@PathVariable usuarioId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscinas obtenidas correctamente",
                data = piscinaService.getPiscinasByUsuarioId(usuarioId)
                    .map { PiscinaMapper.buildPiscinaHeaderResponseDto(it) }
            )
        )
    }

    @GetMapping("/header/{piscinaId}")
    fun getPiscinaHeader(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Piscina para header obtenida correctamente",
                data = PiscinaMapper.buildPiscinaHeaderResponseDto(
                    piscinaService.findById(piscinaId)
                )
            )
        )
    }

    @GetMapping("/resumen/{piscinaId}")
    fun getDataResumenPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Resumen de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaService.findById(piscinaId)
                )
            )
        )
    }

    @GetMapping("/resumenPh/{piscinaId}")
    fun getDataResumenPiscinaPh(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Resumen de PH de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaPhResponseDto(
                    piscinaService.getPh(piscinaId),
                    piscinaService.getDiferenciaPh(piscinaId)
                )
            )
        )
    }

    // Endpoint para retornar el equipamiento de la piscina
    @GetMapping("/equipamiento/{piscinaId}")
    fun getDataEquipamientoPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Equipamiento de la piscina obtenida correctamente",
                data = PiscinaMapper.buildPiscinaEquipamientoResponseDto(
                    piscinaService.findById(piscinaId),
                    piscinaService.getPresion(piscinaId)
                )
            )
        )
    }

    @GetMapping("lectura/{piscinaId}")
    fun getLecturasPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Lecturas de la piscina obtenida correctamente",
                data = piscinaService.getLecturasPiscina(piscinaId)
            )
        )
    }

    @GetMapping("programacion/{piscinaId}")
    fun getProgramacionPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programaciones de la piscina obtenidas correctamente",
                data = PiscinaMapper.buildPiscinaProgramacionResponseDto(piscinaService.findById(piscinaId))
            )
        )
    }

    @PostMapping("/alta")
    fun savePiscina(@RequestBody piscinaDto: PiscinaRequestDto): ResponseEntity<CustomResponse> {
        val plaqueta = plaquetaRepository.findByPatenteAndEstado(piscinaDto.codigoPlaca, UsuarioType.ACTIVO)!!
        val newPiscina = PiscinaMapper.buildPiscina(piscinaDto, plaqueta)
        if (piscinaDto.administradorId != null) {
            newPiscina.administrador = usuarioService.findById(piscinaDto.administradorId).apply {
                if (this == null) {
                    return ResponseEntity.status(400).body(
                        CustomResponse(
                            message = "No se encontró el administrador con id ${piscinaDto.administradorId}",
                            data = null
                        )
                    )
                }
                this.piscinaAsignada()
            }
        }

        piscinaService.create(newPiscina)
        return ResponseEntity.status(201).body(
            CustomResponse(
                message = "Piscina creada correctamente",
                data = PiscinaMapper.buildPiscinaResponseDto(newPiscina)
            )
        )
    }

    @PutMapping("/update-bomba/{piscinaId}")
    fun updateBombaPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody bombaRequestDto: BombaRequestDto
    ): ResponseEntity<CustomResponse> {
        val bombaActualizada = BombaMapper.buildBomba(bombaRequestDto)
        piscinaService.updateBomba(piscinaId, bombaActualizada)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Bomba actualizada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-filtro/{piscinaId}")
    fun updateFiltroPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody filtroDto: FiltroRequestDto
    ): ResponseEntity<CustomResponse> {
        val filtroActualizado = FiltroMapper.buildFiltro(filtroDto)
        piscinaService.updateFiltro(piscinaId, filtroActualizado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Filtro actualizado correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-germicida/{piscinaId}")
    fun updateGermicidaPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody germicidaDto: SistemaGermicidaRequestDto
    ): ResponseEntity<CustomResponse> {
        val germicidaActualizado = SistemaGermicidaMapper.buildSistemaGermicida(germicidaDto)
        piscinaService.updateGermicida(piscinaId, germicidaActualizado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Sistema germicida actualizado correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-calefaccion/{piscinaId}")
    fun updateCalefaccionPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody calefaccionDto: CalefaccionRequestDto
    ): ResponseEntity<CustomResponse> {
        val calefaccionActualizada = CalefaccionMapper.buildCalefaccion(calefaccionDto)
        piscinaService.updateCalefaccion(piscinaId, calefaccionActualizada)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Calefacción actualizada correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-calefaccion/{piscinaId}")
    fun deleteCalefaccion(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        piscinaService.deleteCalefaccion(piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Calefacción eliminada correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-germicida/{piscinaId}/{germicidaId}")
    fun deleteGermicida(
        @PathVariable piscinaId: Long,
        @PathVariable germicidaId: Long
    ): ResponseEntity<CustomResponse> {
        piscinaService.deleteGermicida(piscinaId, germicidaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Germicida eliminado correctamente",
                data = null
            )
        )
    }

    @PostMapping("/add-calefaccion/{piscinaId}")
    fun addCalefaccion(
        @PathVariable piscinaId: Long,
        @RequestBody calefaccionDto: CalefaccionRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevaCalefaccion = CalefaccionMapper.buildCalefaccion(calefaccionDto)
        piscinaService.addCalefaccion(piscinaId, nuevaCalefaccion)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Calefacción agregada correctamente",
                data = null
            )
        )
    }

    @PostMapping("/add-germicida/{piscinaId}")
    fun addGermicida(
        @PathVariable piscinaId: Long,
        @RequestBody germicidaDto: SistemaGermicidaRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevoGermicida = SistemaGermicidaMapper.buildSistemaGermicida(germicidaDto)
        piscinaService.addGermicida(piscinaId, nuevoGermicida)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Germicida agregado correctamente",
                data = null
            )
        )
    }

    @PostMapping("/add-bomba/{piscinaId}")
    fun addBomba(
        @PathVariable piscinaId: Long,
        @RequestBody bombaRequestDto: BombaRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevaBomba = BombaMapper.buildBomba(bombaRequestDto)
        piscinaService.addBomba(piscinaId, nuevaBomba)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Bomba agregada correctamente",
                data = null
            )
        )
    }

    @PutMapping("update-compuestos/{piscinaId}")
    fun updateCompuestosPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody compuestos: PiscinaCompuestosRequestDto
    ): ResponseEntity<CustomResponse> {
        piscinaService.updateCompuestos(piscinaId, compuestos.orp, compuestos.controlPh, compuestos.cloroSalino)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Compuestos de la piscina actualizados correctamente",
                data = null
            )
        )
    }

    @PostMapping("/add-registro/{piscinaId}")
    fun addRegistro(
        @PathVariable piscinaId: Long,
        @RequestBody registroDto: RegistroResponseDto
    ): ResponseEntity<CustomResponse> {
        println(registroDto)
        val registro = RegistroMapper.buildRegistro(registroDto)
        piscinaService.addRegistro(piscinaId, registro)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Registro agregado correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-registro/{piscinaId}")
    fun updateRegistro(
        @PathVariable piscinaId: Long,
        @RequestBody registroDto: RegistroResponseDto
    ): ResponseEntity<CustomResponse> {
        val registroActualizado = RegistroMapper.buildRegistro(registroDto)
        piscinaService.updateRegistro(piscinaId, registroActualizado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Registro actualizado correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-registro/{piscinaId}/{registroId}")
    fun deleteRegistro(@PathVariable piscinaId: Long, @PathVariable registroId: Long): ResponseEntity<CustomResponse> {
        piscinaService.deleteRegistro(piscinaId, registroId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Registro eliminado correctamente",
                data = null
            )
        )
    }

    @PostMapping("/add-programacion/{piscinaId}/{esFiltrado}")
    fun createProgramacionPiscina(
        @PathVariable piscinaId: Long,
        @PathVariable esFiltrado: Boolean,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        if (!esFiltrado) {
            val nuevaProgramacion = ProgramacionMapper.buildProgramacionIluminacion(programacionDTO)
            piscinaService.agregarProgramacionIluminacion(piscinaId, nuevaProgramacion)
        } else {
            val nuevaProgramacion = ProgramacionMapper.buildProgramacionFiltrado(programacionDTO)
            piscinaService.agregarProgramacionFiltrado(piscinaId, nuevaProgramacion)
        }
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación filtrado de la piscina creada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-programacion/{piscinaId}/{esFiltrado}")
    fun updateProgramacion(
        @PathVariable piscinaId: Long,
        @PathVariable esFiltrado: Boolean,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        if (esFiltrado) {
            val programacionActualizada = ProgramacionMapper.buildProgramacionFiltrado(programacionDTO)
            piscinaService.updateProgramacionFiltrado(piscinaId, programacionActualizada)
        } else {
            val programacionActualizada = ProgramacionMapper.buildProgramacionIluminacion(programacionDTO)
            piscinaService.updateProgramacionIluminacion(piscinaId, programacionActualizada)
        }
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina actualizada correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-programacion/{piscinaId}/{programacionId}/{esFiltrado}")
    fun deleteProgramacionFiltrado(
        @PathVariable piscinaId: Long,
        @PathVariable programacionId: Long,
        @PathVariable esFiltrado: Boolean
    ): ResponseEntity<CustomResponse> {
        piscinaService.deleteProgramacion(piscinaId, programacionId,esFiltrado)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación eliminada correctamente",
                data = null
            )
        )
    }


}