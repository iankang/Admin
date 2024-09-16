package com.thinkauth.thinkfusionauth.entities

import io.fusionauth.domain.User
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
data class SentenceAccepted(
    @DocumentReference
    var sentenceUploadEntity: SentenceUploadEntity? = null,
    var acceptor: User? = null
):AuditMetadata()
