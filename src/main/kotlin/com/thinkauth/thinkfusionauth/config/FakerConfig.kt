package com.thinkauth.thinkfusionauth.config

import com.github.javafaker.Faker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FakerConfig {

    @Bean
    fun getFaker(): Faker {
        return Faker()
    }
}