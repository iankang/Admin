package com.thinkauth.thinkfusionauth.entities

import io.fusionauth.domain.User
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
data class SentenceUploadEntity(
    @DocumentReference
    var SentenceEntitie: SentenceEntitie? = null,
    var mediaPathId:String? = null,
    var isAccepted:Boolean? = false
):AuditMetadata()
