package com.estonianport.unique.repository

import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.EstadoType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaquetaRepository : CrudRepository<Plaqueta, Long> {

    fun findByPatenteAndEstado(patente: String, estado: EstadoType): Plaqueta?

    fun findByEstado(estado: EstadoType): List<Plaqueta>?

}