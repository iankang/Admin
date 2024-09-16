package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.SentenceDocumentState
import net.minidev.json.annotate.JsonIgnore
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SentenceDocumentEntity(
    var documentUploadId:String,
    var languageId:String?,
    var languageName:String?,
    var dialectId:String?,
    var dialectName:String?,
    var businessId:String?,
    var businessName:String?,
    var sentenceDocumentState: SentenceDocumentState = SentenceDocumentState.UNPROCESSED
) : AuditMetadata(){
    var fileName:String? = null
    var fileType:String? = null
    var fileSize:String? = null
    @JsonIgnore
    var file:ByteArray? = null
    var sentenceCount:Int? = 0
}