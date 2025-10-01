package com.estonianport.unique.dto.response

import com.estonianport.unique.model.enums.EntradaAguaType
import com.estonianport.unique.model.enums.FuncionFiltroType
import com.estonianport.unique.model.enums.SistemaGermicidaType
import com.fasterxml.jackson.annotation.JsonProperty

data class PiscinaEstadoDto(

    @JsonProperty("id_solicitud")
    val idSolicitud: String? = null,

    @JsonProperty("patente")
    val patente: String,

    @JsonProperty("eaa")
    val entradaAguaActiva: List<EntradaAguaType>,

    @JsonProperty("sist_germ")
    val sistemaGermicidaActivo: List<SistemaGermicidaType>,

    @JsonProperty("ffa")
    val funcionFiltroActivo: FuncionFiltroType,

    @JsonProperty("ca_ac")
    val calefaccionActiva: Boolean
)