package com.thinkauth.thinkfusionauth.services

import io.minio.MinioClient
import org.springframework.stereotype.Service

@Service
class MinioService(
    private val minioClient: MinioClient
) {


}