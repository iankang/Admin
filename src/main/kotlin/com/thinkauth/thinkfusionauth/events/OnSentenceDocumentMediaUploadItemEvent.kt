package com.thinkauth.thinkfusionauth.events

import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.entities.GenderState
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.springframework.context.ApplicationEvent
import org.springframework.web.multipart.MultipartFile

class OnSentenceDocumentMediaUploadItemEvent(
    val file: MultipartFile,
    val business: Business,
    val language:Language?,
    val dialect:Dialect?,
    val fileId:String
): ApplicationEvent(file) {
}