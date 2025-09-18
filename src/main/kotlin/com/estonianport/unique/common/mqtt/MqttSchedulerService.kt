package com.estonianport.unique.common.mqtt

import com.estonianport.unique.model.ErrorLectura
import com.estonianport.unique.repository.ErrorLecturaRepository
import com.estonianport.unique.repository.LecturaRepository
import com.estonianport.unique.repository.PiscinaRepository
import com.estonianport.unique.repository.PlaquetaRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MqttSchedulerService(
    private val plaquetaRepository: PlaquetaRepository,
    private val lecturaRepository: LecturaRepository,
    private val errorLecturaRepository: ErrorLecturaRepository,
    private val piscinaRepository: PiscinaRepository
) {

    @Scheduled(fixedRate = 60 * 1000) // cada 30 minutos
    fun verificarLecturas() {
        val ahora = LocalDateTime.now()
        val limite = ahora.minusMinutes(35)

        val plaquetas = plaquetaRepository.findAll()
        plaquetas.forEach { plaqueta ->
            val ultima = lecturaRepository.findUltimaByPlaqueta(plaqueta.patente)

            if (ultima == null || ultima.fecha.isBefore(limite)) {
                errorLecturaRepository.save(ErrorLectura(piscinaRepository.findByPlaqueta(plaqueta)))
                println("Guardado ErrorLectura para plaqueta ${plaqueta.id}")
            }
        }
    }
}
