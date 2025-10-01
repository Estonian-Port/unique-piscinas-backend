package com.estonianport.unique.common.quartz

import com.estonianport.unique.service.PlaquetaService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProgramacionJob : Job {

    @Autowired
    lateinit var plaquetaService: PlaquetaService

    override fun execute(context: JobExecutionContext) {
        val tipo = context.mergedJobDataMap.getString("tipo")
        val piscinaId = context.mergedJobDataMap.getLong("piscinaId")

        when (tipo) {
//            "FILTRAR" -> plaquetaService.enviarComandoFiltrar(piscinaId)
//            "REPOSO" -> plaquetaService.enviarComandoReposo(piscinaId)
//            "LUZ_ON" -> plaquetaService.encenderLuces(piscinaId)
//            "LUZ_OFF" -> plaquetaService.apagarLuces(piscinaId)
            "FILTRAR" -> println("INICIA PROGRAMACION FILTRADO")
            "REPOSO" -> println("FINALIZA PROGRAMACION FILTRADO")
            "LUZ_ON" -> println("INICIA PROGRAMACION ILUMINACION")
            "LUZ_OFF" -> println("FINALIZA PROGRAMACION ILUMINACION")
        }
    }
}
