package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import org.apache.catalina.User
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class MediaEntityUserUploadState(
    var owner:UserEntity,
    @Indexed
    var phoneNumber:String?,
    @Indexed
    var nationalId:String?,
    @Indexed
    var sentenceId: String?,
    var actualSentence:String?,
    @Indexed
    var languageId:String?,
    var languageName:String?,
    var businessId: String?,
    var genderState: String?,
    @Indexed
    var mediaEntityId:String?,
    var mediaState: MediaAcceptanceState =  MediaAcceptanceState.PENDING,
    var paymentState: PaymentState = PaymentState.UNPAID,
    var uploadDate:LocalDateTime? = null,
    var paymentDate:LocalDateTime? = null,
    var acceptedCount:Long? = null,
    var rejectedCount:Long? = null
):AuditMetadata(){


}
