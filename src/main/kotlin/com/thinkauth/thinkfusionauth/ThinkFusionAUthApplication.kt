package com.thinkauth.thinkfusionauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
class ThinkFusionAUthApplication

fun main(args: Array<String>) {
    runApplication<ThinkFusionAUthApplication>(*args)
}
