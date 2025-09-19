package com.estonianport.unique.service

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.model.Bomba
import com.estonianport.unique.model.Calefaccion
import com.estonianport.unique.model.Filtro
import com.estonianport.unique.model.Ionizador
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.ProgramacionFiltrado
import com.estonianport.unique.model.ProgramacionIluminacion
import com.estonianport.unique.model.Registro
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.Trasductor
import com.estonianport.unique.model.UV
import com.estonianport.unique.model.enums.EntradaAgua
import com.estonianport.unique.model.enums.FuncionFiltro
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service

@Service
class PiscinaService(private val piscinaRepository: PiscinaRepository, private val usuarioService: UsuarioService) {

    fun getPiscinasByUsuarioId(usuarioId: Long): List<Piscina> {
        usuarioService.findById(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")
        return piscinaRepository.findByAdministradorId(usuarioId)
    }

    fun findById(piscinaId: Long): Piscina {
        return piscinaRepository.findById(piscinaId)
            ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")
    }

    fun getLecturasPiscina(piscinaId: Long): List<LecturaConErrorResponseDto> {
        return piscinaRepository.findTodasLecturasConError(piscinaId)
    }

    fun getPresion(piscinaId: Long): Double {
        return piscinaRepository.getPresion(piscinaId)
            ?: 0.0
    }

    fun getPh(piscinaId: Long): Double {
        return piscinaRepository.getPh(piscinaId)
            ?: 0.0
    }

    fun getDiferenciaPh(piscinaId: Long): Double {
        val ultimasDosPh = piscinaRepository.getDiferenciaPh(piscinaId)
            ?: return 0.0

        if (ultimasDosPh.size < 2) {
            return 0.0
        }
        return ultimasDosPh[0] - ultimasDosPh[1]
    }

    fun totalPiscinas(): Int {
        return piscinaRepository.count().toInt()
    }

    fun countPiscinasDesborde(): Int {
        return piscinaRepository.countDesbordante()
    }

    fun countPiscinasSkimmer(): Int {
        return piscinaRepository.countSkimmer()
    }

    fun getVolumenTotal(): Double {
        return piscinaRepository.getTotalVolumen()
    }

    fun getVolumenPromedio(): Double {
        return piscinaRepository.getPromedioVolumen()
    }

    fun getPiscinasRegistradas(): List<Piscina> {
        return piscinaRepository.findAll()
    }

    fun getPiscinasSinAdministrador(): List<Piscina> {
        return piscinaRepository.findByAdministradorIsNull()
    }

    fun desvincularAdministrador(usuarioId: Long, piscinaId: Long) {
        val piscina = findById(piscinaId)
        piscina.administrador = null
        piscinaRepository.save(piscina)
        usuarioService.desvincularPiscina(usuarioId, tienePiscinaAsignada(usuarioId))
    }

    fun tienePiscinaAsignada(usuarioId: Long): Boolean {
        return piscinaRepository.existsByAdministradorId(usuarioId)
    }


    fun create(piscina: Piscina): Piscina {
        return piscinaRepository.save(piscina)
    }

    fun deleteProgramacion(piscinaId: Long, programacionId: Long, esFiltrado: Boolean) {
        val piscina = findById(piscinaId)
        if (esFiltrado) {
            piscina.eliminarProgramacionFiltrado(programacionId)
        } else {
            piscina.eliminarProgramacionLuces(programacionId)
        }
        piscinaRepository.save(piscina)
    }

    fun agregarProgramacionIluminacion(piscinaId: Long, programacion: ProgramacionIluminacion) {
        val piscina = findById(piscinaId)
        piscina.agregarProgramacionIluminacion(programacion)
        piscinaRepository.save(piscina)
    }

    fun agregarProgramacionFiltrado(piscinaId: Long, programacion: ProgramacionFiltrado) {
        val piscina = findById(piscinaId)
        piscina.agregarProgramacionFiltrado(programacion)
        piscinaRepository.save(piscina)
    }

    fun updateProgramacionIluminacion(
        piscinaId: Long,
        programacion: ProgramacionIluminacion,
    ) {
        val piscina = findById(piscinaId)
        val programacionAEditar = piscina.programacionIluminacion.find { it.id == programacion.id }
            ?: throw NotFoundException("La programación de iluminación con ID: ${programacion.id} no pertenece a la piscina con ID: $piscinaId")
        programacionAEditar.apply {
            horaInicio = programacion.horaInicio
            horaFin = programacion.horaFin
            dias = programacion.dias
            activa = programacion.activa
        }
        piscinaRepository.save(piscina)
    }

    fun updateProgramacionFiltrado(
        piscinaId: Long,
        programacion: ProgramacionFiltrado,
    ) {
        val piscina = findById(piscinaId)
        val programacionAEditar = piscina.programacionFiltrado.find { it.id == programacion.id }
            ?: throw NotFoundException("La programación de filtrado con ID: ${programacion.id} no pertenece a la piscina con ID: $piscinaId")
        programacionAEditar.apply {
            horaInicio = programacion.horaInicio
            horaFin = programacion.horaFin
            dias = programacion.dias
            activa = programacion.activa
            funcionFiltro = programacion.funcionFiltro
        }
        piscinaRepository.save(piscina)
    }

    fun asignarAdministrador(usuarioId: Long, piscinaId: Long) {
        val usuario =
            usuarioService.findById(usuarioId) ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")
        val piscina = findById(piscinaId)
        piscina.administrador = usuario
        usuarioService.piscinaAsignada(usuario)
        piscinaRepository.save(piscina)
    }

    fun usuarioEliminado(usuarioId: Long) {
        val piscinas = getPiscinasByUsuarioId(usuarioId)
        piscinas.forEach {
            it.administrador = null
            piscinaRepository.save(it)
        }
    }

    fun updateBomba(piscinaId: Long, bombaActualizada: Bomba) {
        val piscina = findById(piscinaId)
        piscina.bomba.find { it.id == bombaActualizada.id }?.apply {
            marca = bombaActualizada.marca
            modelo = bombaActualizada.modelo
            potencia = bombaActualizada.potencia
            activa = bombaActualizada.activa
        }
        piscinaRepository.save(piscina)
    }

    fun updateFiltro(piscinaId: Long, filtroActualizado: Filtro) {
        val piscina = findById(piscinaId)
        if (piscina.filtro.id != filtroActualizado.id) {
            throw NotFoundException("El filtro con ID: ${filtroActualizado.id} no pertenece a la piscina con ID: $piscinaId")
        }
        piscina.filtro = filtroActualizado //REVISAR ESTO!
        piscinaRepository.save(piscina)
    }

    fun updateGermicida(piscinaId: Long, germicidaActualizada: SistemaGermicida) {
        val piscina = findById(piscinaId)
        val germicida = piscina.sistemaGermicida.find { it.id == germicidaActualizada.id }
            ?: throw NotFoundException("El sistema germicida con ID: ${germicidaActualizada.id} no pertenece a la piscina con ID: $piscinaId")
        germicida.apply {
            marca = germicidaActualizada.marca
        }
        if (germicidaActualizada is UV && germicida is UV) {
            germicida.potencia = germicidaActualizada.potencia
        }
        if (germicidaActualizada is Ionizador && germicida is Ionizador) {
            germicida.electrodos = germicidaActualizada.electrodos
        }
        if (germicidaActualizada is Trasductor && germicida is Trasductor) {
            germicida.potencia = germicidaActualizada.potencia
        }
        piscinaRepository.save(piscina)
    }

    fun updateCalefaccion(piscinaId: Long, calefaccionActualizada: Calefaccion) {
        val piscina = findById(piscinaId)
        piscina.calefaccion?.apply {
            this.modelo = calefaccionActualizada.modelo
            this.marca = calefaccionActualizada.marca
            this.potencia = calefaccionActualizada.potencia
        } ?: throw NotFoundException("La piscina con ID: $piscinaId no tiene calefacción asignada")
        piscinaRepository.save(piscina)
    }

    fun deleteCalefaccion(piscinaId: Long) {
        val piscina = findById(piscinaId)
        if (piscina.calefaccion == null) {
            throw NotFoundException("La piscina con ID: $piscinaId no tiene calefacción asignada")
        }
        piscina.calefaccion = null
        piscinaRepository.save(piscina)
    }

    fun deleteGermicida(piscinaId: Long, germicidaId: Long) {
        val piscina = findById(piscinaId)
        val germicida = piscina.sistemaGermicida.find { it.id == germicidaId }
            ?: throw NotFoundException("El sistema germicida con ID: $germicidaId no pertenece a la piscina con ID: $piscinaId")
        piscina.sistemaGermicida.remove(germicida)
        piscinaRepository.save(piscina)
    }

    fun addBomba(piscinaId: Long, bomba: Bomba) {
        val piscina = findById(piscinaId)
        if (piscina.bomba.size >= 3) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene el máximo de bombas permitidas (3)")
        }
        piscina.bomba.add(bomba)
        piscinaRepository.save(piscina)
    }

    fun addGermicida(piscinaId: Long, germicida: SistemaGermicida) {
        val piscina = findById(piscinaId)
        if (piscina.sistemaGermicida.size >= 3) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene el máximo de sistemas germicidas permitidos (3)")
        }
        validarUnicidadGermicida(germicida, piscina)
        piscina.sistemaGermicida.add(germicida)
        piscinaRepository.save(piscina)
    }

    fun addCalefaccion(piscinaId: Long, calefaccion: Calefaccion) {
        val piscina = findById(piscinaId)
        if (piscina.calefaccion != null) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene una calefacción asignada")
        }
        piscina.calefaccion = calefaccion
        piscinaRepository.save(piscina)
    }

    fun validarUnicidadGermicida(germicida: SistemaGermicida, piscina: Piscina) {
        if (germicida is UV) {
            if (piscina.sistemaGermicida.any { it is UV }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida UV asignado")
            }
        }
        if (germicida is Ionizador) {
            if (piscina.sistemaGermicida.any { it is Ionizador }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida Ionizador asignado")
            }
        }
        if (germicida is Trasductor) {
            if (piscina.sistemaGermicida.any { it is Trasductor }) {
                throw IllegalStateException("La piscina con ID: ${piscina.id} ya tiene un sistema germicida Trasductor asignado")
            }
        }
    }

    fun updateCompuestos(piscinaId: Long, orp: Boolean, controlPh: Boolean, cloroSalino: Boolean) {
        val piscina = findById(piscinaId)
        piscina.orp = orp
        piscina.controlAutomaticoPH = controlPh
        piscina.cloroSalino = cloroSalino
        piscinaRepository.save(piscina)
    }

    fun addRegistro(piscinaId: Long, registro: Registro) {
        val piscina = findById(piscinaId)
        piscina.agregarRegistro(registro)
        piscinaRepository.save(piscina)
    }

    fun updateRegistro(piscinaId: Long, registro: Registro) {
        val piscina = findById(piscinaId)
        val registroExistente = piscina.registros.find { it.id == registro.id }
            ?: throw NotFoundException("El registro con ID: ${registro.id} no pertenece a la piscina con ID: $piscinaId")
        registroExistente.apply {
            fecha = registro.fecha
            dispositivo = registro.dispositivo
            accion = registro.accion
            descripcion = registro.descripcion
            nombreTecnico = registro.nombreTecnico
        }
        piscinaRepository.save(piscina)
    }

    fun deleteRegistro(piscinaId: Long, registroId: Long) {
        val piscina = findById(piscinaId)
        val registroExistente = piscina.registros.find { it.id == registroId }
            ?: throw NotFoundException("El registro con ID: $registroId no pertenece a la piscina con ID: $piscinaId")
        piscina.eliminarRegistro(registroExistente)
        piscinaRepository.save(piscina)
    }

    fun updateEntradaAgua(piscinaId: Long, entradaAgua: MutableList<EntradaAgua>) {
        val piscina = findById(piscinaId)
        println("Entrada de agua nueva: $entradaAgua")
        println("Entrada de agua actual: ${piscina.entradaAgua}")
        piscina.entradaAgua.clear()
        piscina.entradaAgua.addAll(entradaAgua)
        println("Entrada de agua actualizada: ${piscina.entradaAgua}")
        piscinaRepository.save(piscina)
    }

    fun desactivarFuncionActiva(piscinaId: Long) {
        val piscina = findById(piscinaId)
        piscina.funcionActiva.clear()
        piscinaRepository.save(piscina)
    }

    fun updateFuncionActiva(piscinaId: Long, funcionActiva: MutableList<FuncionFiltro>) {
        val piscina = findById(piscinaId)
        piscina.funcionActiva = funcionActiva
        piscinaRepository.save(piscina)
    }

}