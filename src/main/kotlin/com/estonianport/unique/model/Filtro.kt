package com.estonianport.unique.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType

@JsonTypeInfo(
    use= JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo")
@JsonSubTypes(value = [
        JsonSubTypes.Type(value = FiltroArena::class, name = "ARENA"),
        JsonSubTypes.Type(value = FiltroVidrio::class, name = "VIDRIO"),
        JsonSubTypes.Type(value = FiltroCartucho::class, name = "CARTUCHO"),
        JsonSubTypes.Type(value = FiltroDiatomea::class, name = "DIATOMEA")
    ])
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Filtro (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val marca: String,
    val modelo: String,
    val diametro: Double
){

}
@Entity
 class FiltroArena(
    id : Long,
    marca : String,
    modelo : String,
    diametro : Double,
    val cantidadArena: Int
) : Filtro(id,  marca, modelo, diametro)

@Entity
 class FiltroVidrio(
    id : Long,
    marca : String,
    modelo : String,
    diametro : Double,
    val cantidadVidrio: Int
) : Filtro(id,  marca, modelo, diametro)

@Entity
 class FiltroCartucho(
    id : Long,
    marca : String,
    modelo : String,
    diametro : Double,
    val micrasDelCartucho: Int
) : Filtro(id,  marca, modelo, diametro){

}

@Entity
 class FiltroDiatomea(
    id : Long,
    marca : String,
    modelo : String,
    diametro : Double,
) : Filtro(id,  marca, modelo, diametro)



