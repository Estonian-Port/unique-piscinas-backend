package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.SistemaGermicidaRequestDto
import com.estonianport.unique.dto.response.SistemaGermicidaResponseDto
import com.estonianport.unique.model.Ionizador
import com.estonianport.unique.model.SistemaGermicida
import com.estonianport.unique.model.Trasductor
import com.estonianport.unique.model.UV
import com.estonianport.unique.model.enums.CondicionType

object SistemaGermicidaMapper {

    fun buildSistemaGermicidaResponseDto(germicida : SistemaGermicida): SistemaGermicidaResponseDto {
        return SistemaGermicidaResponseDto(
            id = germicida.id.toString(),
            tipo = germicida.tipo(),
            marca = germicida.marca,
            vidaRestante = (germicida.vidaRestante / 60).toString(),
            activo = germicida.activo,
            estado = condicionToString(germicida.condicion!!),
            datoExtra = when (germicida) {
                is UV -> germicida.potencia
                is Ionizador -> germicida.electrodos
                is Trasductor -> germicida.potencia
                else -> 0.0
            }
        )
    }

    private fun condicionToString(condicion: CondicionType): String {
        return when (condicion) {
            CondicionType.OPERATIVO -> "Operativo"
            CondicionType.REQUIERE_REVISION -> "Requiere revisión"
            CondicionType.REEMPLAZO_URGENTE -> "Reemplazo urgente"
            CondicionType.MANTENIMIENTO -> "Mantenimiento"
        }
    }


    fun buildSistemaGermicida (germicida: SistemaGermicidaRequestDto) : SistemaGermicida {
        when (germicida.tipo) {
            "UV" -> {
                return UV(
                    id = germicida.id ?: 0,
                    marca = germicida.marca,
                    estado = CondicionType.OPERATIVO,
                    potencia = germicida.datoExtra,
                    vidaUtil = germicida.vidaUtil
                )
            }
            "IONIZADOR" -> {
                return Ionizador(
                    id = germicida.id ?: 0,
                    marca = germicida.marca,
                    estado = CondicionType.OPERATIVO,
                    electrodos = germicida.datoExtra,
                    vidaUtil = germicida.vidaUtil
                )
            }
            "TRASDUCTOR" -> {
                return Trasductor(
                    id = germicida.id ?: 0,
                    marca = germicida.marca,
                    estado = CondicionType.OPERATIVO,
                    potencia = germicida.datoExtra,
                    vidaUtil = germicida.vidaUtil
                )
            }
            else -> throw IllegalArgumentException("Tipo de sistema germicida no soportado: ${germicida.tipo}")
        }
    }
}