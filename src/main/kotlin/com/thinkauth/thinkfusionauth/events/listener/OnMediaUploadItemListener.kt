package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.StorageService
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.data.rest.core.mapping.ResourceType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

@Component
class OnMediaUploadItemListener(
    private val fileManagerService: StorageService,
    @Value("\${minio.bucket}")
    private val bucketName:String
): ApplicationListener<OnMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnMediaUploadItemListener::class.java)

    @Async
    override fun onApplicationEvent(event: OnMediaUploadItemEvent) {

        logger.info("uploading media")

        uploadMedia(event)
        logger.info("Finished uploading media")
    }

    private fun uploadMedia(event: OnMediaUploadItemEvent){
        val multipartFile = event.file
        val path = event.copyLocation
        val bucko = "$bucketName/"+ BucketName.USER_ACCOUNT_PROFILE+"/"+path.nameWithoutExtension
        logger.info("path: $path")
        fileManagerService.uploadFile(bucketName,bucko,multipartFile.inputStream)
    }
}