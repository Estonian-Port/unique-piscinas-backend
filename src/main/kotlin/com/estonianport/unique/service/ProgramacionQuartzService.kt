package com.estonianport.unique.service

import com.estonianport.unique.common.quartz.QuartzSchedulerService
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import jakarta.annotation.PostConstruct
import org.quartz.JobKey
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgramacionSyncService(
    private val piscinaRepository: PiscinaRepository,
    private val quartzSchedulerService: QuartzSchedulerService,
    private val estadoPiscinaService: EstadoPiscinaService
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun reprogramarJobsQuartz() {
        val piscinas = piscinaRepository.findAll()
        piscinas.forEach { piscina ->
            val patente = piscina.plaqueta.patente
            val programacionesExistentes = piscina.programaciones
            val programaciones = programacionesExistentes.filter { it.activa }

            programaciones.forEach { prog ->
                prog.dias.forEach { dia ->
                    val jobInicio = JobKey.jobKey("inicio_${prog.id}_${dia.name}")
                    val jobFin = JobKey.jobKey("fin_${prog.id}_${dia.name}")

                    if (!quartzSchedulerService.scheduler.checkExists(jobInicio)) {
                        quartzSchedulerService.programarJob(
                            piscina.id, patente,
                            if (prog.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES",
                            dia, prog.horaInicio,
                            jobInicio.name, prog.id!!
                        )
                    }

                    if (!quartzSchedulerService.scheduler.checkExists(jobFin)) {
                        quartzSchedulerService.programarJob(
                            piscina.id, patente,
                            if (prog.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES",
                            dia, prog.horaFin,
                            jobFin.name, prog.id!!
                        )
                    }

                    if (prog.esAhora()) {
                        val comando = if (prog.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES"
                        estadoPiscinaService.aplicarComando(piscina.id, comando, prog.id!!)
                    }
                }
            }

        }
        println("🔁 Quartz sincronizado con programaciones activas.")
    }
}