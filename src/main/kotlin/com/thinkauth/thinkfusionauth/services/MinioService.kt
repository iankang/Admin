package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.utils.BucketName
import io.minio.*
import io.minio.http.Method
import io.minio.messages.Bucket
import io.minio.messages.Item
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.InputStream
import javax.servlet.ServletContext


@Service
class MinioService(
    private val minioClient: MinioClient, private val servletContext: ServletContext
) {

    private val logger = LoggerFactory.getLogger(MinioService::class.java)
    fun bucketExists(bucketName: String): Boolean {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    }

    fun getPresignedUrl(
        bucketName: String, objectName: String
    ): String? {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket(bucketName).method(Method.GET).`object`(objectName).build()
            )
        } catch (e: Exception) {
            logger.error("error: ${e.message}")
        }
        return null
    }

    fun listBuckets(): MutableList<Bucket>? {
        try {

            return minioClient.listBuckets()
        } catch (e: Exception) {
            logger.error("listBuckets", e.message)
        }
        return null
    }

    fun makeBucket(
        bucketName: String
    ) {
        try {
            return minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            )
        } catch (e: Exception) {
            logger.error("makeBucket ${e.message}")
        }
    }

    fun removeBucket(bucketName: String) {
        return minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build())
    }

    fun streamObject(
        bucketName: String, objectName: String
    ): GetObjectResponse? {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
            )
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    fun downloadObject(
        bucketName: String, objectName: String, filename: String
    ) {
        try {
            return minioClient.downloadObject(
                DownloadObjectArgs.builder().bucket(bucketName).`object`(objectName).filename(filename).build()
            )
        } catch (e: Exception) {
            logger.error("downloadObject", e.message)
        }
    }

    fun listObjects(
        bucketName: String
    ): MutableIterable<Result<Item>>? {
        return minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).build()
        )
    }


    fun uploadFile(
        bucketName: String, objectName: String, inputStream: InputStream
    ): ObjectWriteResponse? {
        try {

            return minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).`object`(objectName).stream(inputStream, -1, 10485760)
                    .contentType("application/octet-stream").build()
            )
        } catch (e: Exception) {
            logger.error("uploadVideo: ${e.message}")
        }
        return null
    }

    fun uploadMedia(
        bucketName: String, objectName: String, filename: String, contentType: String
    ): ObjectWriteResponse? {
        try {
            return minioClient.uploadObject(
                UploadObjectArgs.builder().bucket(bucketName).`object`(objectName).filename(filename)
                    .contentType(contentType).build()
            )
        } catch (e: Exception) {
            logger.error("uploadMedia", e.message)
        }
        return null
    }

    fun removeObject(
        bucketName: String, objectName: String
    ) {
        try {


            return minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
            )
        } catch (e: Exception) {
            logger.error("removeObject", e.message)
        }
    }

    fun composeObject(
        bucketName: String, objectName: String
    ): ObjectWriteResponse? {
        try {

            val sourceObjectList: MutableList<ComposeSource> = ArrayList()
            sourceObjectList.add(
                ComposeSource.builder().bucket(bucketName).`object`(BucketName.USER_ACCOUNT_PROFILE.name).build()
            )
            sourceObjectList.add(
                ComposeSource.builder().bucket(bucketName).`object`(BucketName.BUSINESS_PROFILE_PIC.name).build()
            )
            sourceObjectList.add(
                ComposeSource.builder().bucket(bucketName).`object`(BucketName.STT_UPLOAD.name).build()
            )
            sourceObjectList.add(
                ComposeSource.builder().bucket(bucketName).`object`(BucketName.VOICE_COLLECTION.name).build()
            )
            sourceObjectList.add(
                ComposeSource.builder().bucket(bucketName).`object`(BucketName.PROMPT_COLLECTION.name).build()
            )

            return minioClient.composeObject(
                ComposeObjectArgs.builder().bucket(bucketName).`object`(objectName).sources(sourceObjectList).build()
            )
        } catch (e: Exception) {
            logger.error("composeObject: ${e.message}")
        }
        return null
    }

    fun getStats(
        bucketName: String, objectName: String
    ): StatObjectResponse? {
        try {


            return minioClient.statObject(
                StatObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
            )
        } catch (e: Exception) {
            logger.error("getStats", e.message)
        }
        return null

    }

    fun getMinioObject(
        bucketName: String, objectName: String
    ): InputStreamResource {
        val inputStream: InputStream = minioClient.getObject(
            GetObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
        )
        return InputStreamResource(inputStream)
    }

    fun getMinioObjectInputStream(
        bucketName: String, objectName: String
    ): InputStream {
        val inputStream: InputStream = minioClient.getObject(
            GetObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
        )
        return inputStream
    }


}