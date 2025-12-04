package com.estonianport.unique.common.quartz

import com.estonianport.unique.model.Programacion
import com.estonianport.unique.model.enums.ProgramacionType
import org.quartz.JobKey
import org.springframework.stereotype.Component

@Component
class ProgramacionJobManager(
    private val quartzSchedulerService: QuartzSchedulerService
) {

    fun crearJobs(piscinaId: Long, patente: String, programacion: Programacion) {
        programacion.dias.forEach { dia ->
            val comandoInicio = if (programacion.tipo == ProgramacionType.FILTRADO) "FILTRAR" else "ENCENDER_LUCES"
            val comandoFin = if (programacion.tipo == ProgramacionType.FILTRADO) "REPOSO" else "APAGAR_LUCES"

            val jobInicioId = "inicio_${programacion.id}_${dia.name}_${programacion.horaInicio}_${if (programacion.tipo == ProgramacionType.FILTRADO) "F" else "L"}"
            val jobFinId = "fin_${programacion.id}_${dia.name}_${programacion.horaFin}_${if (programacion.tipo == ProgramacionType.FILTRADO) "F" else "L"}"

            // Evita duplicar jobs
            if (!quartzSchedulerService.scheduler.checkExists(JobKey.jobKey(jobInicioId))) {
                quartzSchedulerService.programarJob(
                    piscinaId, patente, comandoInicio, dia, programacion.horaInicio, jobInicioId, programacion.id!!
                )
            }

            if (!quartzSchedulerService.scheduler.checkExists(JobKey.jobKey(jobFinId))) {
                quartzSchedulerService.programarJob(
                    piscinaId, patente, comandoFin, dia, programacion.horaFin, jobFinId, programacion.id!!
                )
            }
        }

        println("✅ Jobs Quartz creados para programación ${programacion.id}")
    }

    fun eliminarJobs(programacion: Programacion) {
        programacion.dias.forEach { dia ->
            quartzSchedulerService.eliminarJob("inicio_${programacion.id}_${dia.name}_${programacion.horaInicio}_${if (programacion.tipo == ProgramacionType.FILTRADO) "F" else "L"}")
            quartzSchedulerService.eliminarJob("fin_${programacion.id}_${dia.name}_${programacion.horaFin}_${if (programacion.tipo == ProgramacionType.FILTRADO) "F" else "L"}")
        }
        println("🧹 Jobs Quartz eliminados para programación ${programacion.id}")
    }

    fun recrearJobs(piscinaId: Long, patente: String, programacion: Programacion) {
        eliminarJobs(programacion)
        crearJobs(piscinaId, patente, programacion)
    }
}
