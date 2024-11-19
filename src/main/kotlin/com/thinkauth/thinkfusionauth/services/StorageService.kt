package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.interfaces.StorageServiceInterface
import com.thinkauth.thinkfusionauth.utils.BucketName
import io.minio.GetObjectResponse
import io.minio.ObjectWriteResponse
import io.minio.Result
import io.minio.StatObjectResponse
import io.minio.messages.Bucket
import io.minio.messages.Item
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths

@Service
class StorageService(
    private val minioService: MinioService
) : StorageServiceInterface {

    private val logger = LoggerFactory.getLogger(StorageService::class.java)

    override fun upload(
        bucketName: String, objectName: String, filename: String, contentType: String
    ): ObjectWriteResponse? {
        try {
            return minioService.uploadMedia(bucketName, objectName, filename, contentType)

        } catch (e: Exception) {
            logger.error("upload", e.message)
        }
        return null
    }

    override fun uploadFile(bucketName: String, objectName: String, inputStream: InputStream): ObjectWriteResponse? {
        try {
            return minioService.uploadFile(bucketName, objectName, inputStream)
        } catch (e: Exception) {
            logger.error("upload", e.message)
        }
        return null
    }

    override fun download(bucketName: String, objectName: String, filename: String) {
        try {
            minioService.downloadObject(bucketName, objectName, filename)
        } catch (e: Exception) {
            logger.error("download", e.message)
        }
    }

    override fun stream(bucketName: String, objectName: String): GetObjectResponse? {
        try {
            return minioService.streamObject(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("stream", e.message)
        }
        return null
    }

    override fun createBucket(bucketName: String) {
        try {
            minioService.makeBucket(bucketName)
        } catch (e: Exception) {
            logger.error("makeBucket", e.message)
        }
    }

    override fun listBuckets(): MutableList<Bucket>? {
        try {
            return minioService.listBuckets()
        } catch (e: Exception) {
            logger.error("listBuckets", e.message)
        }
        return null
    }

    override fun listObjects(bucketName: String): MutableIterable<Result<Item>>? {
        try {
            return minioService.listObjects(bucketName)
        } catch (e: Exception) {
            logger.error("listObjects", e.message)
        }
        return null
    }

    override fun composeObject(bucketName: String, objectName: String): ObjectWriteResponse? {
        try {
            return minioService.composeObject(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("composeObject: ${e.message}")
        }
        return null
    }

    override fun getObject(bucketName: String, objectName: String): InputStreamResource? {
        try {
            return minioService.getMinioObject(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("getObject: ${e.toString()}")
        }
        return null
    }

    fun getObjectInputStream(bucketName: String, objectName: String): InputStream? {
        try {
            return minioService.getMinioObjectInputStream(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("getObject: ${e.toString()}")
        }
        return null
    }

    override fun fetchStats(bucketName: String, objectName: String): StatObjectResponse? {
        try {
            return minioService.getStats(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("getObject: ${e.message}")
        }
        return null
    }

    override fun removeObject(bucketName: String, objectName: String) {
        try {
             minioService.removeObject(bucketName, objectName)
        } catch (e: Exception) {
            logger.error("getObject: ${e.message}")
        }
    }


}