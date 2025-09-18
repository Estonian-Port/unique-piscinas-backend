package com.estonianport.unique.repository

import com.estonianport.unique.model.Lectura
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LecturaRepository : CrudRepository<Lectura, Long> {

    @Query("SELECT l FROM Lectura l WHERE l.piscina.plaqueta.patente = :patente ORDER BY l.fecha DESC LIMIT 1")
    fun findUltimaByPlaqueta(patente: String): Lectura?
}

