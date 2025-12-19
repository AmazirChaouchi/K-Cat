package com.n7.api.kcat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KcatApplication

fun main(args: Array<String>) {
	runApplication<KcatApplication>(*args)
}
