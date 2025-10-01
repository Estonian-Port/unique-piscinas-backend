package com.estonianport.unique.common.quartz

import com.estonianport.unique.model.Programacion
import com.estonianport.unique.model.enums.ProgramacionType
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.TimeZone

@Service
class ProgramacionQuartzService(private val scheduler: Scheduler) {

    fun programar(prog: Programacion, piscinaId: Long) {
        if (!prog.activa) return

        // Borro jobs existentes (si hay)
        val startKey = JobKey("start-${prog.id}", "programaciones")
        val endKey = JobKey("end-${prog.id}", "programaciones")
        if (scheduler.checkExists(startKey)) scheduler.deleteJob(startKey)
        if (scheduler.checkExists(endKey)) scheduler.deleteJob(endKey)

        val tipoInicio = when (prog.tipo) {
            ProgramacionType.FILTRADO -> "FILTRAR"
            ProgramacionType.ILUMINACION -> "LUZ_ON"
        }
        val tipoFin = when (prog.tipo) {
            ProgramacionType.FILTRADO -> "REPOSO"
            ProgramacionType.ILUMINACION -> "LUZ_OFF"
        }

        val startJob = JobBuilder.newJob(ProgramacionJob::class.java)
            .withIdentity(startKey)
            .usingJobData("tipo", tipoInicio)
            .usingJobData("piscinaId", piscinaId)
            .build()

        val startTrigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger-start-${prog.id}", "programaciones")
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cronFrom(prog.horaInicio, prog.dias))
                .inTimeZone(TimeZone.getDefault()))
            .forJob(startJob)
            .build()

        scheduler.scheduleJob(startJob, startTrigger)

        val endJob = JobBuilder.newJob(ProgramacionJob::class.java)
            .withIdentity(endKey)
            .usingJobData("tipo", tipoFin)
            .usingJobData("piscinaId", piscinaId)
            .build()

        val endTrigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger-end-${prog.id}", "programaciones")
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cronFrom(prog.horaFin, prog.dias))
                .inTimeZone(TimeZone.getDefault()))
            .forJob(endJob)
            .build()

        scheduler.scheduleJob(endJob, endTrigger)
    }

    fun eliminar(programacionId: Long) {
        scheduler.deleteJob(JobKey("start-$programacionId", "programaciones"))
        scheduler.deleteJob(JobKey("end-$programacionId", "programaciones"))
    }

    private fun cronFrom(time: LocalTime, dias: List<DayOfWeek>): String {
        val segundos = time.second
        val minutos = time.minute
        val horas = time.hour
        val diasQuartz = if (dias.isEmpty()) {
            "MON-SUN"
        } else {
            dias.joinToString(",") { it.name.substring(0, 3) } // MONDAY -> MON
        }
        // Quartz cron: sec min hour dayOfMonth month dayOfWeek
        return "$segundos $minutos $horas ? * $diasQuartz"
    }
}