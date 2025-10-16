package com.estonianport.unique.service

import com.estonianport.unique.common.quartz.QuartzSchedulerService
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgramacionSyncService(
    private val piscinaRepository: PiscinaRepository,
    private val quartzSchedulerService: QuartzSchedulerService
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun reprogramarJobsQuartz() {
        val piscinas = piscinaRepository.findAll()
        piscinas.forEach { piscina ->
            val patente = piscina.plaqueta.patente
            val programacionesExistentes = piscina.programacionFiltrado + piscina.programacionIluminacion
            val programaciones = programacionesExistentes.filter { it.activa }

            programaciones.forEach { prog ->
                prog.dias.forEach { dia ->
                    quartzSchedulerService.programarJob(
                        piscina.id,
                        patente,
                        if (prog.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES",
                        dia,
                        prog.horaInicio,
                        "inicio_${prog.id}_${dia.name}"
                    )
                    quartzSchedulerService.programarJob(
                        piscina.id,
                        patente,
                        if (prog.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES",
                        dia,
                        prog.horaFin,
                        "fin_${prog.id}_${dia.name}"
                    )
                }
            }
        }
        println("🔁 Quartz sincronizado con programaciones activas.")
    }
}