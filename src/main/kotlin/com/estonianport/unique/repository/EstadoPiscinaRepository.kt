package com.estonianport.unique.repository

import com.estonianport.unique.model.EstadoPiscina
import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.enums.EstadoType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EstadoPiscinaRepository : CrudRepository<EstadoPiscina, Long>{

    @Query("SELECT e FROM EstadoPiscina e WHERE e.piscina.id = :piscinaId ORDER BY e.fecha DESC LIMIT 1")
    fun findEstadoActualByPiscinaId(piscinaId: Long): EstadoPiscina?
}