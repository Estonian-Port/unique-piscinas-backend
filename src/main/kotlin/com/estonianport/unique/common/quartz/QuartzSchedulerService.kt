package com.estonianport.unique.common.quartz

import org.quartz.*
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalTime

@Service
class QuartzSchedulerService(val scheduler: Scheduler) {

    fun programarJob(
        piscinaId: Long,
        patente: String,
        comando: String,
        diaSemana: DayOfWeek,
        hora: LocalTime,
        jobId: String,
        programacionId: Long
    ) {
        val jobDetail = JobBuilder.newJob(EnviarComandoPiscinaJob::class.java)
            .withIdentity(jobId)
            .usingJobData("piscinaId", piscinaId)
            .usingJobData("patente", patente)
            .usingJobData("comando", comando)
            .usingJobData("programacionId", programacionId)
            .build()

        val quartzDayOfWeek = when (diaSemana) {
            DayOfWeek.MONDAY -> 2
            DayOfWeek.TUESDAY -> 3
            DayOfWeek.WEDNESDAY -> 4
            DayOfWeek.THURSDAY -> 5
            DayOfWeek.FRIDAY -> 6
            DayOfWeek.SATURDAY -> 7
            DayOfWeek.SUNDAY -> 1
        }

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("${jobId}_trigger")
            .withSchedule(
                CronScheduleBuilder.weeklyOnDayAndHourAndMinute(
                    quartzDayOfWeek,
                    hora.hour,
                    hora.minute
                )
            )
            .build()


        scheduler.scheduleJob(jobDetail, trigger)
        println("🕒 Quartz Job programado -> $jobId ($comando ${diaSemana.name} ${hora})")
    }

    fun eliminarJob(jobId: String) {
        val jobKey = JobKey.jobKey(jobId)
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey)
            println("🗑️ Quartz Job eliminado -> $jobId")
        }
    }
}

