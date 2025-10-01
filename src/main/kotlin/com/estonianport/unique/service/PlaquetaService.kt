package com.estonianport.unique.service

import com.estonianport.unique.model.enums.EstadoType
import com.estonianport.unique.repository.PlaquetaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaquetaService() {

    @Autowired
    lateinit var plaquetaRepository: PlaquetaRepository

    fun getPatentesDisponibles(): List<String> {
        return plaquetaRepository.findPatentesByEstadoInactivo()
    }

    fun activarPlaqueta(patente: String) {
        val plaqueta = plaquetaRepository.findByPatente(patente) ?: throw Exception("Plaqueta no encontrada")
        plaqueta.estado = EstadoType.ACTIVO
        plaquetaRepository.save(plaqueta)
    }
}