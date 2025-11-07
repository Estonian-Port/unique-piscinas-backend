package com.estonianport.unique.common.quartz

import com.estonianport.unique.common.errors.NotFoundException
import com.estonianport.unique.model.enums.ProgramacionType
import com.estonianport.unique.repository.PiscinaRepository
import com.estonianport.unique.service.EstadoPiscinaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobExecutionService(
    private val piscinaRepository: PiscinaRepository,
    private val estadoPiscinaService: EstadoPiscinaService
) {

    @Transactional
    fun ejecutarComandoProgramacion(piscinaId: Long, comando: String, programacionId: Long) {
        val piscina = piscinaRepository.findById(piscinaId)
            ?: throw NotFoundException("Piscina no encontrada con ID: $piscinaId")

        val programacion = piscina.programaciones.find { it.id == programacionId }
            ?: throw NotFoundException("Programación no encontrada con ID: $programacionId")

        // ✅ Validar si está activa
        if (!programacion.activa) {
            println("⚠️ Programación $programacionId INACTIVA, no se ejecuta")
            return
        }

        // ✅ Si está pausada manualmente
        if (programacion.pausadaManualmente && programacion.tipo == ProgramacionType.ILUMINACION) {
            println("⏸️ Programación $programacionId PAUSADA manualmente")

            if (comando in listOf("APAGAR_LUCES")) {
                println("🔄 Reseteando pausa para el próximo ciclo")
                programacion.pausadaManualmente = false
                piscinaRepository.save(piscina)
            }

            return
        }

        // 🔹 Ejecutar comando
        estadoPiscinaService.aplicarComando(piscinaId, comando, programacionId)
    }
}