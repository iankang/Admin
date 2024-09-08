package com.thinkauth.thinkfusionauth.events

import com.thinkauth.thinkfusionauth.entities.GenderState
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.springframework.context.ApplicationEvent
import org.springframework.web.multipart.MultipartFile

class OnSentenceDocumentMediaUploadItemEvent(
    val file: MultipartFile,
    val copyLocation: String,
    val resource: BucketName,
    val businessId: String?,
    val languageId:String?,
    val dialectId:String?,
): ApplicationEvent(file) {
}