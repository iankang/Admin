package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MediaEntity(
    var mediaName: String?,
    var owner: UserEntity,
    var mediaObject: String,
    var mediaPathId: String,
    val sentenceId: String?,
    val businessId: String?

):AuditMetadata()
