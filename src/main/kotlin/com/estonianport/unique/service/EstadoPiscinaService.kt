package com.estonianport.unique.service

import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.repository.EstadoPiscinaRepository
import org.springframework.stereotype.Service

@Service
class EstadoPiscinaService(
    private val estadoPiscinaRepository: EstadoPiscinaRepository,
    private val piscinaService: PiscinaService
) {

    fun findEstadoActualByPiscinaId(piscinaId: Long): EstadoPiscina {
        return estadoPiscinaRepository.findEstadoActualByPiscinaId(piscinaId) ?: EstadoPiscina.estadoInicial(
            piscinaService.findById(piscinaId)
        )
    }

    fun updateEntradaAgua(piscinaId: Long, entradaAgua: MutableList<EntradaAguaType>) {
        val piscina = findEstadoActualByPiscinaId(piscinaId)
        piscina.entradaAguaActiva.clear()
        piscina.entradaAguaActiva.addAll(entradaAgua)
        estadoPiscinaRepository.save(piscina)
    }

    fun desactivarFuncionActiva(piscinaId: Long) {
        val piscina = findEstadoActualByPiscinaId(piscinaId)
        piscina.funcionFiltroActivo = FuncionFiltroType.REPOSO
        estadoPiscinaRepository.save(piscina)
    }

    fun updateFuncionActiva(piscinaId: Long, funcionActiva: FuncionFiltroType) {
        val piscina = findEstadoActualByPiscinaId(piscinaId)
        piscina.funcionFiltroActivo = funcionActiva
        estadoPiscinaRepository.save(piscina)
    }
}