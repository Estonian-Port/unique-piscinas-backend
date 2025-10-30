package com.estonianport.unique.service

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.common.quartz.QuartzSchedulerService
import com.estonianport.unique.model.Bomba
import com.estonianport.unique.model.Calefaccion
import com.estonianport.unique.model.Filtro
import com.estonianport.unique.model.FiltroArena
import com.estonianport.unique.model.FiltroCartucho
import com.estonianport.unique.model.FiltroVidrio
import com.estonianport.unique.model.Ionizador
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Registro
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.Trasductor
import com.estonianport.unique.model.UV
import com.estonianport.unique.repository.PiscinaRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import kotlin.math.ceil

@Service
class PiscinaService(
    private val piscinaRepository: PiscinaRepository,
    private val usuarioService: UsuarioService,
) {

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
        val resultados = piscinaRepository.findTodasLecturasConError(piscinaId)
        return resultados.map {
            LecturaConErrorResponseDto(
                id = (it[0] as Number).toLong(),
                fecha = (it[1] as Timestamp).toLocalDateTime().toString(),
                ph = (it[2] as? Number)?.toDouble(),
                cloro = (it[3] as? Number)?.toDouble(),
                redox = (it[4] as? Number)?.toDouble(),
                presion = (it[5] as? Number)?.toDouble(),
                esError = it[6] as Boolean
            )

        }
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

    fun getProximaProgramacionFiltrado(piscinaId: Long): LocalDateTime? {
        val results = piscinaRepository.findProgramacionesFiltradoActivas(piscinaId)
        if (results.isEmpty()) return null
        val ahora = LocalDateTime.now()
        var proxima: LocalDateTime? = null
        for (row in results) {
            val dia = DayOfWeek.valueOf(row[0].toString())
            val hora =
                LocalTime.parse(row[1].toString()) // Calcular el próximo día correspondiente
            var candidato = ahora.with(TemporalAdjusters.nextOrSame(dia)).with(hora)
            if (candidato.isBefore(ahora)) {
                candidato = candidato.plusWeeks(1)
            }
            if (proxima == null || candidato.isBefore(proxima)) {
                proxima = candidato
            }
        }
        return proxima
    }

    fun calcularTiempoRestante(piscinaId: Long): String? {
        val proximaEjecucion = getProximaProgramacionFiltrado(piscinaId) ?: return null
        val ahora = LocalDateTime.now()
        if (proximaEjecucion.isBefore(ahora)) {
            return "El ciclo ya comenzó"
        }
        val duracion = Duration.between(ahora, proximaEjecucion)
        val dias = duracion.toDays()
        val horas = duracion.toHours() % 24
        val minutos = ceil(duracion.seconds / 60.0).toLong() % 60
        return when {
            dias > 0 -> "En ${dias}d ${horas}h ${minutos}m"
            horas > 0 -> "En ${horas}h ${minutos}m"
            minutos > 0 -> "En ${minutos}m"
            else -> "En menos de un minuto"
        }
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
        return piscinaRepository.findAllByOrderByDireccionAsc()
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

    fun asignarAdministrador(usuarioId: Long, piscinaId: Long) {
        val usuario =
            usuarioService.findById(usuarioId)
                ?: throw NotFoundException("Usuario no encontrado con ID: $usuarioId")
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
        }
        piscinaRepository.save(piscina)
    }

    fun updateFiltro(piscinaId: Long, filtroActualizado: Filtro) {
        val piscina = findById(piscinaId)
        if (piscina.filtro.id != filtroActualizado.id) {
            throw NotFoundException("El filtro con ID: ${filtroActualizado.id} no pertenece a la piscina con ID: $piscinaId")
        }
        piscina.filtro.apply {
            marca = filtroActualizado.marca
            modelo = filtroActualizado.modelo
            diametro = filtroActualizado.diametro
            if (this is FiltroArena && filtroActualizado is FiltroArena) {
                this.cantidadArena = filtroActualizado.cantidadArena
            }
            if (this is FiltroVidrio && filtroActualizado is FiltroVidrio) {
                this.cantidadVidrio = filtroActualizado.cantidadVidrio
            }
            if (this is FiltroCartucho && filtroActualizado is FiltroCartucho) {
                this.micrasDelCartucho = filtroActualizado.micrasDelCartucho
            }
        }
        piscinaRepository.save(piscina)
    }

    fun resetearContadorFiltro(piscinaId: Long, filtroId: Long) {
        val piscina = findById(piscinaId)
        if (piscina.filtro.id != filtroId) {
            throw NotFoundException("El filtro con ID: $filtroId no pertenece a la piscina con ID: $piscinaId")
        }
        piscina.filtro.fechaAlta = LocalDate.now()
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

    fun resetearContadorGermicida(piscinaId: Long, germicidaId: Long) {
        val piscina = findById(piscinaId)
        val germicida = piscina.sistemaGermicida.find { it.id == germicidaId }
            ?: throw NotFoundException("El sistema germicida con ID: $germicidaId no pertenece a la piscina con ID: $piscinaId")
        germicida.resetearVida()
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
        if (piscina.bomba.size >= 4) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene el máximo de bombas permitidas (4)")
        }
        if (piscina.bomba.any { it.tipo == bomba.tipo }) {
            throw IllegalStateException("La piscina con ID: $piscinaId ya tiene una bomba del tipo ${bomba.tipo} asignada")
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

    fun getPatentePlaqueta(piscinaId: Long): String {
        val piscina = findById(piscinaId)
        return piscina.plaqueta.patente
    }

}