package br.com.digital.order

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class Application {

	@Value("\${spring.jackson.time-zone}")
	private lateinit var timeZone: String

	@PostConstruct
	fun setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone))
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
