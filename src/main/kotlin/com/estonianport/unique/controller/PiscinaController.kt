package com.estonianport.unique.controller

import com.estonianport.unique.common.mqtt.MqttPublisherService
import com.estonianport.unique.dto.request.BombaRequestDto
import com.estonianport.unique.dto.request.CalefaccionRequestDto
import com.estonianport.unique.dto.request.FiltroRequestDto
import com.estonianport.unique.dto.request.FuncionFiltroRequestDto
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
import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.repository.PlaquetaRepository
import com.estonianport.unique.service.EstadoPiscinaService
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.ProgramacionType
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

@RestController
@RequestMapping("/piscina")
@CrossOrigin("*")
class PiscinaController {

    @Autowired
    private lateinit var mqttPublisherService: MqttPublisherService

    @Autowired
    private lateinit var plaquetaService: PlaquetaService

    @Autowired
    private lateinit var estadoPiscinaService: EstadoPiscinaService

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
                    piscinaService.findById(piscinaId),
                    estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
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
                    piscinaService.getPresion(piscinaId),
                    estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId),
                    piscinaService.getProximoCicloFiltrado(piscinaId)
                )
            )
        )
    }

    @GetMapping("lecturas/{piscinaId}")
    fun getLecturasPiscina(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        val lecturas = piscinaService.getLecturasPiscina(piscinaId)
        println(lecturas)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Lecturas de la piscina obtenida correctamente",
                data = piscinaService.getLecturasPiscina(piscinaId)
            )
        )
    }

    @PostMapping("/lectura-manual/{piscinaId}")
    fun tomarLecturaManual(@PathVariable piscinaId: Long): ResponseEntity<CustomResponse> {
        val piscina = piscinaService.findById(piscinaId)
        val patentePlaqueta = piscina.plaqueta.patente
        // Recibimos un ok cuando la muestra fue realizada con exito?
        mqttPublisherService.sendCommand(patentePlaqueta, "tomar_muestra")
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Lectura tomada correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaService.findById(piscinaId),
                    estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
                )
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
        val plaqueta = plaquetaRepository.findByPatenteAndEstado(piscinaDto.codigoPlaca, EstadoType.INACTIVO)!!
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
        plaquetaService.activarPlaqueta(piscinaDto.codigoPlaca)
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

    @PostMapping("/add-programacion/{piscinaId}")
    fun createProgramacionPiscina(
        @PathVariable piscinaId: Long,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        val nuevaProgramacion = ProgramacionMapper.buildProgramacion(programacionDTO)
        piscinaService.agregarProgramacion(piscinaId, nuevaProgramacion)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación filtrado de la piscina creada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-programacion/{piscinaId}")
    fun updateProgramacion(
        @PathVariable piscinaId: Long,
        @RequestBody programacionDTO: ProgramacionRequestDto
    ): ResponseEntity<CustomResponse> {
        val programacionActualizada = ProgramacionMapper.buildProgramacion(programacionDTO)
        piscinaService.updateProgramacion(piscinaId, programacionActualizada)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación de la piscina actualizada correctamente",
                data = null
            )
        )
    }

    @DeleteMapping("/delete-programacion/{piscinaId}/{programacionId}")
    fun deleteProgramacionFiltrado(
        @PathVariable piscinaId: Long,
        @PathVariable programacionId: Long,
    ): ResponseEntity<CustomResponse> {
        piscinaService.deleteProgramacion(piscinaId, programacionId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Programación eliminada correctamente",
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
        val piscinaprueba = PiscinaMapper.buildPiscinaResumenResponseDto(
            piscinaService.findById(piscinaId),
            estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
        )
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
        if (nuevaFuncionActiva == FuncionFiltroType.REPOSO) {
            estadoPiscinaService.desactivarFuncionActiva(piscinaId)
            return ResponseEntity.status(200).body(
                CustomResponse(
                    message = "Función activa desactivada correctamente",
                    data = null
                )
            )
        }

        estadoPiscinaService.updateFuncionActiva(piscinaId, nuevaFuncionActiva)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Función activa actualizada correctamente",
                data = null
            )
        )
    }

    @PutMapping("/update-estado-filtro/{piscinaId}")
    fun updateEstadoFiltro(
        @PathVariable piscinaId: Long,
        @RequestBody funcionActiva: FuncionFiltroRequestDto,
        @RequestBody entradaAgua: List<String>
    ): ResponseEntity<CustomResponse> {
        val patentePlaqueta = piscinaService.getPatentePlaqueta(piscinaId)
        val nuevaFuncionActiva = FuncionFiltroType.valueOf(funcionActiva.funcion.uppercase())
        val nuevasEntradas = entradaAgua.map { EntradaAguaType.valueOf(it.uppercase()) }.toMutableList()
        mqttPublisherService.sendCommandList(patentePlaqueta, nuevasEntradas, nuevaFuncionActiva)
        val ultimoEstado = mqttPublisherService.sendCommand(patentePlaqueta, "tomar_muestra")
        // SI VAMOS A GUARDAR TODOS LOS ESTADOS SIMPLEMENTE SE HACE UN SAVE, SI ES UN SOLO ESTADO QUE SE ACTUALIZA
        // HAY QUE HACER UPDATE DE TODOS LOS DATOS
        val estadoPiscina = estadoPiscinaService.findEstadoActualByPiscinaId(piscinaId)
        return ResponseEntity.status(200).body(
            CustomResponse(
                message = "Comando enviado correctamente",
                data = PiscinaMapper.buildPiscinaResumenResponseDto(
                    piscinaService.findById(piscinaId),
                    estadoPiscina
                )
            )
        )
    }


}