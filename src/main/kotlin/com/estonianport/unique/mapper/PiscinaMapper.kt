package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.PiscinaRequestDto
import com.estonianport.unique.dto.response.*
import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.model.enums.toCapitalized
import java.time.Duration
import java.time.LocalDateTime

object PiscinaMapper{

    fun buildPiscinaHeaderResponseDto(piscina: Piscina): PiscinaListResponseDto {
        return PiscinaListResponseDto(
            id = piscina.id.toString(),
            direccion = piscina.direccion,
            volumen = piscina.volumen
        )
    }

    fun buildPiscinaResumenResponseDto(piscina: Piscina, estadoPiscina: EstadoPiscina): PiscinaResumenResponseDto {
        return PiscinaResumenResponseDto(
            id = piscina.id.toString(),
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            clima = piscina.climaLocal().toString(),
            entradaAgua = estadoPiscina.entradaAguaActiva.map { it.toCapitalized() }.toList(),
            funcionActiva = estadoPiscina.funcionFiltroActivo,
            sistemasGermicidas = piscina.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(it) },
            calefaccion = piscina.calefaccion?.let { CalefaccionMapper.buildCalefaccionResponseDto(it) },
            esDesbordante = piscina.esDesbordante,
        )
    }

    fun buildPiscinaPhResponseDto(ph: Double, diferenciaPh: Double): PiscinaResumenPhResponseDto {
        return PiscinaResumenPhResponseDto(
            ph = ph,
            diferenciaPh = diferenciaPh,
        )
    }

    fun buildPiscinaEquipamientoResponseDto(piscina: Piscina, presionPiscina: Double, estadoPiscina: EstadoPiscina, proximoCicloFiltrado: String?): PiscinaEquipamientoResponseDto {
        return PiscinaEquipamientoResponseDto(
            id = piscina.id.toString(),
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            estadoFiltro = piscina.filtro.activo,
            entradaAgua = estadoPiscina.entradaAguaActiva.map { it.toCapitalized() }.toList(),
            funcionActiva = estadoPiscina.funcionFiltroActivo,
            presion = presionPiscina,
            ultimaActividad = calcularUltimaActividad(estadoPiscina.ultimaActividad),
            proximoCiclo = proximoCicloFiltrado,
            bombas = piscina.bomba.map { BombaMapper.buildBombaResponseDto(it) }.toList(),
            filtro = FiltroMapper.buildFiltroResponseDto(piscina.filtro),
            sistemasGermicidas = piscina.sistemaGermicida.map {
                SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(
                    it
                )
            }.toList(),
        )
    }

    fun calcularUltimaActividad(ultimaActividad : LocalDateTime?) : String {
        if (ultimaActividad == null) {
            return "Sin actividad registrada"
        }
        val ahora = LocalDateTime.now()
        val duracion = Duration.between(ultimaActividad, ahora)
        val minutos = duracion.toMinutes()
        val horas = duracion.toHours()
        val dias = duracion.toDays()

        return when {
            dias > 0 -> "Hace ${dias}d ${horas}h ${minutos}m"
            horas > 0 -> "Hace ${horas}h ${minutos}m"
            minutos > 0 -> "Hace ${minutos}m"
            else -> "Hace menos de un minuto"
        }
    }

    fun buildPiscinaProgramacionResponseDto(piscina: Piscina): PiscinaProgramacionResponseDto {
        val todas = piscina.programaciones.toList()
        val iluminacion = todas.filter { it.tipo == ProgramacionType.ILUMINACION }
            .map { ProgramacionMapper.buildProgramacionResponseDto(it) }
        val filtrado = todas.filter { it.tipo == ProgramacionType.FILTRADO }
            .map { ProgramacionMapper.buildProgramacionResponseDto(it) }

        return PiscinaProgramacionResponseDto(
            id = piscina.id.toString(),
            direccion = piscina.direccion,
            volumen = piscina.volumen.toString(),
            programacionIluminacion = iluminacion,
            programacionFiltrado = filtrado,
            iluminacionManual = piscina.estadoActual()?.luces ?: false
        )
    }

    fun buildPiscinaRegistradaListDto(piscina: Piscina, ph: Double?): PiscinaRegistradaListResponseDto {
        return PiscinaRegistradaListResponseDto(
            id = piscina.id,
            direccion = piscina.direccion,
            esDesbordante = piscina.esDesbordante,
            if (piscina.administrador != null) {
                piscina.administrador!!.nombre + ' ' + piscina.administrador!!.apellido
            } else {
                "Sin administrador"
            },
            ph = ph ?: 0.0,
            sistemasGermicidas = piscina.sistemaGermicida.map {
                SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(
                    it
                )
            },
        )
    }

    fun buildPiscinaFichaTecnicaDto(piscina: Piscina): PiscinaFichaTecnicaDto {
        return PiscinaFichaTecnicaDto(
            id = piscina.id,
            direccion = piscina.direccion,
            ciudad = piscina.ciudad,
            nombreAdministrador = piscina.administrador?.nombre + ' ' + piscina.administrador?.apellido,
            codigoPlaca = piscina.plaqueta.patente,
            esDesbordante = piscina.esDesbordante,
            largo = piscina.largo,
            ancho = piscina.ancho,
            profundidad = piscina.profundidad,
            volumen = piscina.volumen,
            volumenTC = piscina.volumenTC,
            notas = piscina.notas
        )
    }

    fun buildPiscinaAsignadaResponseDto(piscina: Piscina): PiscinaAsignadaResponseDto {
        return PiscinaAsignadaResponseDto(
            id = piscina.id,
            direccion = piscina.direccion,
            esDesbordante = piscina.esDesbordante,
            volumen = piscina.volumen.toString(),
        )
    }

    fun buildPiscinaResponseDto(piscina: Piscina): PiscinaResponseDto {
        return PiscinaResponseDto(
            id = piscina.id,
            direccion = piscina.direccion,
            ciudad = piscina.ciudad,
            esDesbordante = piscina.esDesbordante,
            largo = piscina.largo,
            ancho = piscina.ancho,
            profundidad = piscina.profundidad,
            volumen = piscina.volumen,
            volumenTC = piscina.volumenTC,
            bomba = piscina.bomba.map { BombaMapper.buildBombaResponseDto(it) },
            filtro = FiltroMapper.buildFiltroResponseDto(piscina.filtro),
            sistemaGermicida = piscina.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(it) },
            calefaccion = piscina.calefaccion?.let { CalefaccionMapper.buildCalefaccionResponseDto(it) },
            cloroSalino = piscina.cloroSalino,
            controlAutomaticoPH = piscina.controlAutomaticoPH,
            orp = piscina.orp
        )
    }

    fun buildPiscinaEquiposResponseDto(piscina: Piscina): PiscinaEquiposDto {
        return PiscinaEquiposDto(
            id = piscina.id,
            direccion = piscina.direccion,
            bombas = piscina.bomba.map { BombaMapper.buildBombaResponseDto(it) },
            filtro = FiltroMapper.buildFiltroResponseDto(piscina.filtro),
            sistemasGermicidas = piscina.sistemaGermicida.map {
                SistemaGermicidaMapper.buildSistemaGermicidaResponseDto(
                    it
                )
            },
            cloroSalino = piscina.cloroSalino,
            controlAutomaticoPH = piscina.controlAutomaticoPH,
            orp = piscina.orp,
            calefaccion = piscina.calefaccion?.let { CalefaccionMapper.buildCalefaccionResponseDto(it) },
            registros = piscina.registros.map { RegistroMapper.buildRegistroResponseDto(it) }
        )
    }

    fun buildPiscina(piscinaDTO: PiscinaRequestDto, plaqueta : Plaqueta): Piscina {
        return Piscina(
            id = piscinaDTO.id ?: 0,
            direccion = piscinaDTO.direccion,
            ciudad = piscinaDTO.ciudad,
            esDesbordante = piscinaDTO.esDesbordante,
            largo = piscinaDTO.largo,
            ancho = piscinaDTO.ancho,
            profundidad = piscinaDTO.profundidad,
            volumen = piscinaDTO.volumen,
            volumenTC = piscinaDTO.volumenTC,
            bomba = piscinaDTO.bomba.map { BombaMapper.buildBomba(it) }.toMutableList(),
            filtro = FiltroMapper.buildFiltro(piscinaDTO.filtro),
            sistemaGermicida = piscinaDTO.sistemaGermicida.map { SistemaGermicidaMapper.buildSistemaGermicida(it) }
                .toMutableSet(),
            calefaccion = piscinaDTO.calefaccion?.let { CalefaccionMapper.buildCalefaccion(it) },
            cloroSalino = piscinaDTO.cloroSalino,
            controlAutomaticoPH = piscinaDTO.controlAutomaticoPH,
            orp = piscinaDTO.orp,
            plaqueta = plaqueta,
            notas = piscinaDTO.notas,
        )
    }

}