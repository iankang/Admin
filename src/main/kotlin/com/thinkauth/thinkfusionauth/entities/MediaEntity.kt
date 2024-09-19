package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

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
    var mediaState: MediaAcceptanceState =  MediaAcceptanceState.PENDING
):AuditMetadata(){

    fun toMediaEntityUserUpload(
    ):MediaEntityUserUploadState{
        val mediaUploadState = MediaEntityUserUploadState(
            owner = owner,
            phoneNumber = owner.mobilePhone,
            nationalId = owner.nationalId,
            sentenceId = sentenceId,
            actualSentence = actualSentence,
            languageId = languageId,
            languageName = languageName,
            businessId = businessId,
            genderState = owner.genderState,
            mediaEntityId = id,
            mediaState = mediaState
        )
        mediaUploadState.uploadDate = LocalDateTime.now()
        return mediaUploadState
    }
}
