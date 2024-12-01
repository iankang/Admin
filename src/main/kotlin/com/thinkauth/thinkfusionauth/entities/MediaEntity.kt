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
    var translatedText:String?,
    var languageId:String?,
    var languageName:String?,
    var businessId: String?,
    var genderState: String?,
    var mediaState: MediaAcceptanceState =  MediaAcceptanceState.PENDING
):AuditMetadata(){

    var archived:Boolean = false
    var duration:Float? = null
    var constituencyId:String? = null
    var constituencyName:String? = null
    var countyId:Int? = 0
    var countyName:String? = null
    var dialectId:String? = null
    var educationLevel: String? = null
    var employmentState: String? = null
    var ownerEmail:String? = null
    var nationalId:Long? = null


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
        mediaUploadState.constituencyId = constituencyId
        mediaUploadState.constituencyName = constituencyName
        mediaUploadState.countyId = countyId
        mediaUploadState.countyName = countyName
        mediaUploadState.dialectId = dialectId
        mediaUploadState.educationLevel = educationLevel
        mediaUploadState.employmentState = employmentState
        mediaUploadState.genderState = genderState
        mediaUploadState.languageId = languageId
        mediaUploadState.nationalId = nationalId.toString()
        return mediaUploadState
    }
}
