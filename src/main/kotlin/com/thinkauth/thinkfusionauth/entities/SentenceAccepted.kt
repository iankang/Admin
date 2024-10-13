package com.thinkauth.thinkfusionauth.entities

import io.fusionauth.domain.User
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import java.time.LocalDateTime

@Document
data class SentenceAccepted(
    var sentenceId:String? = null,
    var dialectId:String? = null,
    var dialectName:String? = null,
    var languageId:String? = null,
    var businessId:String? = null,
    var businessName:String? = null,
    var mediaItemId:String? = null,
):AuditMetadata(){
    var acceptedDate:LocalDateTime? = LocalDateTime.now()
}
