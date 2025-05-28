package com.estonianport.unique

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UniqueApplication

fun main(args: Array<String>) {
	runApplication<UniqueApplication>(*args)
}
