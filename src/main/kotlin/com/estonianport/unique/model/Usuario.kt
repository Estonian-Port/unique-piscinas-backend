package com.estonianport.unique.model

import com.estonianport.unique.dto.UsuarioAbmDTO
import com.estonianport.unique.model.enums.UsuarioType
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Usuario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val nombre: String,

    @Column
    val apellido: String,

    @Column
    val celular: Long,

    @Column
    val email: String,

    @Column
    val esAdministrador: Boolean = false,

    @Column
    var estado: UsuarioType = UsuarioType.PENDIENTE,
) {

    @Column
    var password: String? = null

    @Column
    val fechaAlta: LocalDate = LocalDate.now()

    @Column
    var fechaBaja: LocalDate? = null

    @Column
    var ultimoIngreso: LocalDate? = null

    fun toUsuarioAbmDto(): UsuarioAbmDTO {
        return UsuarioAbmDTO(id, nombre, apellido)
    }

    fun confirmarPrimerLoguin() {
        if (ultimoIngreso != null) {
            estado = UsuarioType.INACTIVO
        }
    }
}