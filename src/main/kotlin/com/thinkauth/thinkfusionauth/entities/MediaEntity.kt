package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MediaEntity(
    var mediaName: String?,
    var owner: UserEntity,
    var username:String?,
    var mediaPathId: String,
    var sentenceId: String?,
    var actualSentence:String?,
    var languageId:String?,
    var languageName:String?,
    var businessId: String?,
    var genderState: String?,
    var accepted:Boolean? = false,
    var mediaState: MediaAcceptanceState =  MediaAcceptanceState.PENDING
):AuditMetadata()
