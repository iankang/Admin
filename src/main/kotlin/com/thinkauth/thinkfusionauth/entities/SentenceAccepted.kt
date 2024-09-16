package com.thinkauth.thinkfusionauth.entities

import io.fusionauth.domain.User
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import java.time.LocalDateTime

@Document
data class AudioAcceptedEntity(
    @DocumentReference
    var sentenceUploadEntity: SentenceUploadEntity? = null,
    var acceptor: User? = null
):AuditMetadata(){
    var acceptedDate:LocalDateTime? = LocalDateTime.now()
}
