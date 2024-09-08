package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.utils.BucketName
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.web.multipart.MultipartFile

@Document
class SentenceDocumentEntity(
    var documentUploadId:String,
    var languageId:String?,
    var dialectId:String?,
    var sentenceDocumentState:SentenceDocumentState = SentenceDocumentState.UNPROCESSED
) : AuditMetadata(){
    var fileName:String? = null
    var fileType:String? = null
    var fileSize:String? = null
    var file:ByteArray? = null
}