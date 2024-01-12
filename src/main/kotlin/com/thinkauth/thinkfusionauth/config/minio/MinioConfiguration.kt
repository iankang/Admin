package com.thinkauth.thinkfusionauth.config.minio

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MinioConfiguration(
    @Value("\${minio.url}")
    private val endpoint:String,

    @Value("\${minio.access-key}")
    private val accessKey:String,

    @Value("\${minio.secret-key}")
    private val secretKey:String,
) {

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint,9000,false)
            .credentials(accessKey, secretKey)
            .build()
    }
}