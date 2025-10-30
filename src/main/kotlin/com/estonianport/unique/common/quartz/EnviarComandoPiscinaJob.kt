package com.estonianport.unique.common.quartz

import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import com.estonianport.unique.service.EstadoPiscinaService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
class EnviarComandoPiscinaJob(
    private val estadoPiscinaService: EstadoPiscinaService,
    private val piscinaRepository: PiscinaRepository
) : Job {

    override fun execute(context: JobExecutionContext) {
        val piscinaId = context.mergedJobDataMap["piscinaId"] as Long
        val comando = context.mergedJobDataMap["comando"] as String
        val programacionId = context.mergedJobDataMap["programacionId"] as Long

        try {
            val piscina = piscinaRepository.findById(piscinaId) ?: return
            val programacion = piscina.programaciones.find { it.id == programacionId } ?: return

            // Validar si está activa
            if (!programacion.activa) {
                println("⚠️ Programación $programacionId INACTIVA, no se ejecuta")
                return
            }

            // Si está pausada manualmente → NO ejecutar NINGÚN comando
            if (programacion.pausadaManualmente && programacion.tipo == ProgramacionType.ILUMINACION) {
                println("⏸️ Programación $programacionId PAUSADA manualmente")

                // 🔹 Si es el job de FIN, resetear la pausa para el próximo ciclo
                if (comando in listOf("APAGAR_LUCES")) {
                    println("🔄 Reseteando pausa para el próximo ciclo")
                    programacion.pausadaManualmente = false
                    piscinaRepository.save(piscina)
                }

                // NO ejecutar el comando
                return
            }

            // 🔹 Si llegó hasta aquí, ejecutar el comando normalmente
            estadoPiscinaService.aplicarComando(piscinaId, comando, programacionId)

        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
        }
    }
}
