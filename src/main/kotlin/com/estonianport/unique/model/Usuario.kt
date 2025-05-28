package com.estonianport.unique.model

import com.estonianport.unique.dto.UsuarioAbmDTO
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
    val email: String) {

    @Column
    val username: String? = null

    @Column
    var password: String? = null

    @Column
    val fechaNacimiento: LocalDate? = null

    @Column
    val fechaAlta : LocalDate = LocalDate.now()

    @Column
    var fechaBaja : LocalDate? = null

    fun toUsuarioAbmDto(): UsuarioAbmDTO {
        return UsuarioAbmDTO(id, nombre, apellido, username)
    }
}