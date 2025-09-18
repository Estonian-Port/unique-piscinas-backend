package com.estonianport.unique

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class UniqueApplication

fun main(args: Array<String>) {
	runApplication<UniqueApplication>(*args)
}
