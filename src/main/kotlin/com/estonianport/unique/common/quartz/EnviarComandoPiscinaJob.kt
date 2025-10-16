package com.estonianport.unique.common.quartz

import com.estonianport.unique.common.mqtt.MqttPublisherService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
class EnviarComandoPiscinaJob(
    private val mqttPublisherService: MqttPublisherService
) : Job {

    override fun execute(context: JobExecutionContext) {
        val piscinaId = context.mergedJobDataMap["piscinaId"] as Long
        val patente = context.mergedJobDataMap["patente"] as String
        val comando = context.mergedJobDataMap["comando"] as String

        println("💧 Ejecutando job Quartz: $comando para piscina $piscinaId")
        //mqttPublisherService.sendCommand(patente, comando)
    }
}
