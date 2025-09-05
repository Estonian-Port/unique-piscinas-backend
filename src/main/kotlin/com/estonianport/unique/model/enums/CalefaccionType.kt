package com.estonianport.unique.model.enums

enum class CalefaccionType {
    BOMBA_CALOR, CALENTADOR_GAS;

    fun toDisplayString(): String = when (this) {
        BOMBA_CALOR -> "Bomba Calor"
        CALENTADOR_GAS -> "Calentador Gas"
    }
}