package de.hka.environmentservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EnvironmentServiceApplication

fun main(args: Array<String>) {
    runApplication<EnvironmentServiceApplication>(*args)
}
