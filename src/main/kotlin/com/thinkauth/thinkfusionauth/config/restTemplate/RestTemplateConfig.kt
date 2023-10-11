package com.thinkauth.thinkfusionauth.config.restTemplate

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate


@Configuration
class RestTemplateConfig(
    private val requestResponseLoggingInterceptor: RequestResponseLoggingInterceptor
) {

    //Override timeouts in request factory
    private fun getClientHttpRequestFactory(): HttpComponentsClientHttpRequestFactory {
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(100000)

        //Read timeout
        clientHttpRequestFactory.setReadTimeout(100000)


        return clientHttpRequestFactory
    }
    @Bean
    fun restTemplate(): RestTemplate? {
        val factory = getClientHttpRequestFactory()
//        factory.setConnectTimeout(5000)
//        factory.setReadTimeout(5000)

        val restTemplate = RestTemplate(factory)
        restTemplate.interceptors = listOf(requestResponseLoggingInterceptor)

        return restTemplate
    }
}