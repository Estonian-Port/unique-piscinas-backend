package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.fasterxml.jackson.annotation.JsonProperty

data class PiscinaEstadoDto(
    @JsonProperty("id_solicitud")
    val idSolicitud: String,
    val entradaAguaActiva: List<EntradaAguaType>,
    val sistemaGermicidaActivo: List<SistemaGermicidaType>,
    val funcionFiltroActivo: FuncionFiltroType,
    val calefaccionActiva: Boolean
)