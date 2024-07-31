package com.thinkauth.thinkfusionauth.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AppConfiguration {
    @Bean
    fun cookieProcessorCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer<TomcatServletWebServerFactory> { factory: TomcatServletWebServerFactory ->
            // also listen on http
            val connector: Connector = Connector()
            connector.setPort(httpPort)
            factory.addAdditionalTomcatConnectors(connector)
        }
    }

    @Value("\${server.http.port}")
    private val httpPort = 0
}