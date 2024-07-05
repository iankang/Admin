package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MediaEntity(
    var mediaName: String?,
    var owner: UserEntity,
    var mediaObject: String,
    var mediaPathId: String,
    var sentenceId: String?,
    var businessId: String?,
    var accepted:Boolean? = false,
    var mediaState:MediaAcceptanceState =  MediaAcceptanceState.PENDING
):AuditMetadata()
