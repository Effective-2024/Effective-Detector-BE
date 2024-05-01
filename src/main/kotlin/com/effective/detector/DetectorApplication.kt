package com.effective.detector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DetectorApplication

fun main(args: Array<String>) {
    runApplication<DetectorApplication>(*args)
}
