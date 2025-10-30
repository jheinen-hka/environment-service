package de.hka.environmentservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ContextApplication

fun main(args: Array<String>) {
    runApplication<ContextApplication>(*args)
}