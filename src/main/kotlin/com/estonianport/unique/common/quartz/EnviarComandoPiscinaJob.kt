package com.estonianport.unique.common.quartz

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
class EnviarComandoPiscinaJob(
    private val jobExecutionService: JobExecutionService
) : Job {

    override fun execute(context: JobExecutionContext) {
        val piscinaId = context.mergedJobDataMap["piscinaId"] as Long
        val comando = context.mergedJobDataMap["comando"] as String
        val programacionId = context.mergedJobDataMap["programacionId"] as Long

        println("🔔 Job Quartz ejecutándose: $comando para programación $programacionId")

        try {
            jobExecutionService.ejecutarComandoProgramacion(piscinaId, comando, programacionId)
        } catch (e: Exception) {
            println("❌ Error ejecutando job: ${e.message}")
            e.printStackTrace()
        }
    }
}
