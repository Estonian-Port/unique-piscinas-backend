package com.estonianport.unique.repository

import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.EstadoType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaquetaRepository : CrudRepository<Plaqueta, Long> {

    fun findByPatente(patente: String): Plaqueta?

    fun findByPatenteAndEstado(patente: String, estado: EstadoType): Plaqueta?

    fun findByEstado(estado: EstadoType): List<Plaqueta>?

    @Query("SELECT p.patente FROM Plaqueta p WHERE p.estado = 'INACTIVO'")
    fun findPatentesByEstadoInactivo() : List<String>

}