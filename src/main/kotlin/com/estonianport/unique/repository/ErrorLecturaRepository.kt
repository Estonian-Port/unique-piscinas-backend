package com.estonianport.unique.repository

import com.estonianport.unique.model.ErrorLectura
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ErrorLecturaRepository : CrudRepository<ErrorLectura, Long>
