package com.estonianport.unique.service

import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.repository.EstadoPiscinaRepository
import org.springframework.stereotype.Service

@Service
class EstadoPiscinaService(private val estadoPiscinaRepository: EstadoPiscinaRepository,
                           private val piscinaService: PiscinaService
){

    fun findEstadoActualByPiscinaId(piscinaId: Long): EstadoPiscina {
        return estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId)?: EstadoPiscina.estadoInicial(piscinaService.findById(piscinaId))
    }
}