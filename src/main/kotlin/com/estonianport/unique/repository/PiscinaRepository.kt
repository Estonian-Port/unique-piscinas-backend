package com.estonianport.unique.repository

import com.estonianport.unique.dto.response.LecturaConErrorResponseDto
import com.estonianport.unique.model.Piscina
import com.estonianport.unique.model.Plaqueta
import com.estonianport.unique.model.Programacion
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
        """SELECT id, fecha, ph, cloro, redox, presion, false AS esError
            FROM lectura
            WHERE piscina_id = :piscinaId

            UNION ALL
            
            SELECT id, fecha, NULL, NULL, NULL, NULL, true AS esError
            FROM error_lectura
            WHERE piscina_id = :piscinaId
            
            ORDER BY fecha""",
        nativeQuery = true
    )
    fun findTodasLecturasConError(piscinaId: Long): List<Array<Any>>


    @Query(
        """
        SELECT COUNT(p) FROM Piscina p WHERE p.esDesbordante IS TRUE
    """
    )
    fun countDesbordante(): Int

    @Query(
        """
        SELECT COUNT(p) FROM Piscina p WHERE p.esDesbordante IS FALSE
    """
    )
    fun countSkimmer(): Int

    @Query(
        """
        SELECT SUM(p.volumen) FROM Piscina p
    """
    )
    fun getTotalVolumen(): Double

    @Query(
        """
       SELECT ROUND(AVG(p.volumen), 2) FROM Piscina p
    """
    )
    fun getPromedioVolumen(): Double

    @Query(
        """
    SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Piscina p WHERE p.administrador IS NOT NULL AND p.administrador.id = :usuarioId
    """
    )
    fun existsByAdministradorId(usuarioId: Long): Boolean

    fun findByPlaqueta(plaqueta: Plaqueta): Piscina

    @Query(
        """
    SELECT pf.*
    FROM programacion_filtrado pf
    WHERE pf.piscina_id = :piscinaId
      AND (pf.dia > EXTRACT(DOW FROM CURRENT_DATE)
           OR (pf.dia = EXTRACT(DOW FROM CURRENT_DATE) AND pf.hora > CURRENT_TIME))
    ORDER BY pf.dia, pf.hora
    LIMIT 1
    """, nativeQuery = true
    )
    fun getProximaProgramacionFiltrado(piscinaId: Long): Programacion?
}