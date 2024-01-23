package com.thinkauth.thinkfusionauth.entities

import io.fusionauth.domain.User
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
data class SentenceRejected(
    @DocumentReference
    var sentenceUploadEntity: SentenceUploadEntity? = null,
    var rejector: User? = null
):AuditMetadata()
