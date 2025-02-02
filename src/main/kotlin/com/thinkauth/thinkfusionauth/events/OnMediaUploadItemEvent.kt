package com.thinkauth.thinkfusionauth.events

import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.springframework.context.ApplicationEvent
import org.springframework.web.multipart.MultipartFile

class OnMediaUploadItemEvent(
    val file: MultipartFile,
    val copyLocation: String,
    val resource: BucketName,
    val sentenceId: String?,
    val businessId: String?,
    val user:UserEntity?
): ApplicationEvent(file) {
}