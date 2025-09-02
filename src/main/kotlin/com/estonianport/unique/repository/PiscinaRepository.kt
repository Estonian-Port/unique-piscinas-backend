package com.estonianport.unique.repository

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.model.Piscina
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PiscinaRepository : JpaRepository<Piscina, Int> {

    // Estoy usando JPA para inferir las querys pero habria que ver de hacerlas a mano
    fun findByAdministradorId(usuarioId: Long): List<Piscina>

    fun findById(piscinaId: Long): Piscina?

    fun findByAdministradorIsNull(): List<Piscina>

    @Query(
        """
            SELECT presion
            FROM lectura
            WHERE piscina_id = :piscinaId
            ORDER BY fecha DESC
            LIMIT 1""", nativeQuery = true
    )
    fun getPresion(piscinaId: Long): Double?

    @Query(
        """
            SELECT ph
            FROM lectura
            WHERE piscina_id = :piscinaId
            ORDER BY fecha DESC
            LIMIT 1""", nativeQuery = true
    )
    fun getPh(piscinaId: Long): Double?

    @Query(
        """
            SELECT ph
            FROM lectura
            WHERE piscina_id = :piscinaId
            ORDER BY fecha DESC
            LIMIT 2""", nativeQuery = true
    )
    fun getDiferenciaPh(piscinaId: Long): List<Double>?


    @Query(
        """SELECT id, fecha, ph, cloro, temperatura, presion, false AS esError
            FROM lectura
            WHERE piscina_id = :piscinaId

            UNION ALL
            
            SELECT id, fecha, NULL, NULL, NULL, NULL, true AS esError
            FROM error_lectura
            WHERE piscina_id = :piscinaId
            
            ORDER BY fecha""",
        nativeQuery = true
    )
    fun findTodasLecturasConError(piscinaId: Long): List<LecturaConErrorResponseDto>


    @Query("""
        SELECT COUNT(p) FROM Piscina p WHERE p.esDesbordante IS TRUE
    """)
    fun countDesbordante(): Int

    @Query("""
        SELECT COUNT(p) FROM Piscina p WHERE p.esDesbordante IS FALSE
    """)
    fun countSkimmer(): Int

    @Query("""
        SELECT SUM(p.volumen) FROM Piscina p
    """)
    fun getTotalVolumen(): Double

    @Query("""
       SELECT ROUND(AVG(p.volumen), 2) FROM Piscina p
    """)
    fun getPromedioVolumen(): Double
}