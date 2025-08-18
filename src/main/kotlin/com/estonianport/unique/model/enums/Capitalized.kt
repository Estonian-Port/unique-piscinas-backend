package com.estonianport.unique.model.enums

fun Enum<*>.toCapitalized(): String =
    name.lowercase().replaceFirstChar { it.uppercase() }