package com.estonianport.unique.repository

import com.estonianport.unique.model.Piscina
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PiscinaRepository : JpaRepository<Piscina, Int> {

    // Estoy usando JPA para inferir las querys pero habria que ver de hacerlas a mano
    fun findByUsuarioId(usuarioId: Long): List<Piscina>

    fun findById(piscinaId: Long): Piscina?

}
