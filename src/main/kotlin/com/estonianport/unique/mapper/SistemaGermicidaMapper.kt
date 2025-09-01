package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.SistemaGermicidaRequestDto
import com.estonianport.unique.dto.response.SistemaGermicidaResponseDto
import com.estonianport.unique.model.Ionizador
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.Trasductor
import com.estonianport.unique.model.UV
import com.estonianport.unique.model.enums.EstadoType

object SistemaGermicidaMapper {

    fun buildSistemaGermicidaResponseDto(germicida : SistemaGermicida): SistemaGermicidaResponseDto {
        return SistemaGermicidaResponseDto(
            id = germicida.id.toString(),
            tipo = germicida.tipo(),
            marca = germicida.marca,
            vidaRestante = germicida.vidaRestante().toString(),
            activo = germicida.activo,
            estado = germicida.estado(),
            datoExtra = when (germicida) {
                is UV -> germicida.potencia
                is Ionizador -> germicida.electrodos
                is Trasductor -> germicida.potencia
                else -> 0.0
            }
        )
    }

    fun buildSistemaGermicida (germicida: SistemaGermicidaRequestDto) : SistemaGermicida {
        when (germicida.tipo) {
            "UV" -> {
                return UV(
                    id = germicida.id,
                    activo = germicida.activo,
                    marca = germicida.marca,
                    estado = EstadoType.OPERATIVO,
                    potencia = germicida.datoExtra,
                )
            }
            "IONIZADOR" -> {
                return Ionizador(
                    id = germicida.id,
                    activo = germicida.activo,
                    marca = germicida.marca,
                    estado = EstadoType.OPERATIVO,
                    electrodos = germicida.datoExtra,
                )
            }
            "TRASDUCTOR" -> {
                return Trasductor(
                    id = germicida.id,
                    activo = germicida.activo,
                    marca = germicida.marca,
                    estado = EstadoType.OPERATIVO,
                    potencia = germicida.datoExtra,
                )
            }
            else -> throw IllegalArgumentException("Tipo de sistema germicida no soportado: ${germicida.tipo}")
        }
    }
}