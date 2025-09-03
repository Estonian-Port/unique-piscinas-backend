package com.estonianport.unique.mapper

import com.estonianport.unique.dto.request.BombaRequestDto
import com.estonianport.unique.dto.response.BombaResponseDto
import com.estonianport.unique.model.Bomba

object BombaMapper {

    fun buildBombaResponseDto(bomba: Bomba) : BombaResponseDto {
        return BombaResponseDto(
            id = bomba.id.toString(),
            marca = bomba.marca,
            modelo = bomba.modelo,
            potencia = bomba.potencia.toString(),
            activa = bomba.activa,
        )
    }

    fun buildBomba(bombaDto: BombaRequestDto) : Bomba {
        return Bomba(
            id = bombaDto.id ?: 0,
            marca = bombaDto.marca,
            modelo = bombaDto.modelo,
            potencia = bombaDto.potencia,
            activa = bombaDto.activa,
        )
    }
}