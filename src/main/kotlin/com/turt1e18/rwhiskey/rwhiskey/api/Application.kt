package com.turt1e18.rwhiskey.rwhiskey.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableAsync
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
