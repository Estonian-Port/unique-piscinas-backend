package com.estonianport.unique.repository

import com.estonianport.unique.model.Usuario
import com.estonianport.unique.model.enums.RolType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsuarioRepository : CrudRepository<Usuario, Long> {

    fun findOneByEmail(email: String): Usuario?

    // TODO una vez leidos y entendido, borrar los comentarios, y hay q arreglar si vamos a usar "usuarios"
    // o "usuario", al poner "usuarios" seria como decir "all usuario" o "lista usuario" me da igual usar cualquiera de los dos

    // TODO Creo que ahi estaria, tiene q ser fecha ultimo ingreso not null, fecha baja null y tener asignada
    @Query("""
    SELECT COUNT(u)
    FROM Usuario u
    WHERE u.estado = 'ACTIVO'
    AND u.rol = :rolUser
    AND u.fechaBaja IS NULL
""")
    fun countUsuariosActivos(rolUser: RolType): Int

    @Query("""
    SELECT COUNT(u)
    FROM Usuario u
    WHERE u.estado = 'INACTIVO'
    AND u.rol = :rolUser
    AND u.fechaBaja IS NULL
""")
    fun countUsuariosInactivos(rolUser: RolType): Int

    @Query("""
    SELECT COUNT(u)
    FROM Usuario u
    WHERE u.estado = 'PENDIENTE'
    AND u.rol = :rolUser
    AND u.fechaBaja IS NULL
""")
    fun countUsuariosPendientes(rolUser: RolType): Int

    @Query(
        """
        SELECT COUNT(u) 
        FROM Usuario u
        Where u.rol = :rolUser
        AND u.fechaBaja IS NULL
    """
    )
    fun totalUsuarios(rolUser: RolType): Int

    // TODO y aca dejo la q devolveria usuarios, seria optimo q si la vas a usar solo para listar en front
    // devuelva usuarioDto
    @Query(
        """
        SELECT DISTINCT u
        FROM Usuario u
        JOIN Piscina p ON p.administrador = u
        WHERE u.ultimoIngreso IS NOT NULL
        AND u.fechaBaja IS NULL
    """
    )
    fun getUsuariosActivos(): List<Usuario>

    /*
    @Query("SELECT new com.estonianport.unique.dto.UsuarioAbmDTO(c.usuario.id, c.usuario.nombre, " +
            "c.usuario.apellido, c.usuario.username) FROM Cargo c WHERE c.empresa.id = :empresaId AND " +
            "c.fechaBaja IS NULL")
    fun getAllUsuario(empresaId: Long, pageable: PageRequest) : Page<UsuarioAbmDTO>

    @Query("SELECT new com.estonianport.agendaza.dto.UsuarioAbmDTO(c.usuario.id, c.usuario.nombre, " +
            "c.usuario.apellido, c.usuario.username) FROM Cargo c WHERE c.empresa.id = :empresaId " +
            "AND (c.usuario.nombre ILIKE %:buscar% OR c.usuario.apellido ILIKE %:buscar%) AND c.fechaBaja IS NULL")
    fun getAllUsuarioFiltrados(empresaId : Long, buscar : String, pageable : Pageable) : Page<UsuarioAbmDTO>

    @Query("SELECT COUNT(c) FROM Cargo c WHERE c.empresa.id = :empresaId AND c.fechaBaja IS NULL")
    fun getCantidadUsuario(empresaId : Long) : Int

    @Query("SELECT COUNT(c) FROM Cargo c WHERE c.empresa.id = :empresaId AND " +
            "(c.usuario.nombre ILIKE %:buscar% OR c.usuario.apellido ILIKE %:buscar%) AND c.fechaBaja IS NULL")
    fun getCantidadUsuarioFiltrados(empresaId : Long, buscar: String) : Int
    */

    fun getUsuarioByEmail(email: String): Usuario?

    fun getUsuarioByCelular(celular: Long): Usuario?

    override fun findById(id: Long): Optional<Usuario>
}