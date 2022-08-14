package task.service

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
@EnableAutoConfiguration
class ServiceApplication

fun main(args: Array<String>) {
  runApplication<ServiceApplication>(*args)
}