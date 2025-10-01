package com.estonianport.unique.repository

import com.estonianport.unique.model.Programacion
import org.springframework.data.repository.CrudRepository

interface ProgramacionRepository : CrudRepository<Programacion, Long> {
    fun findByActivaTrue(): List<Programacion>
}