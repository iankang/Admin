package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import org.apache.catalina.User
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class MediaEntityUserApprovalState(
    var owner:UserEntity,
    var approverFullName:String?,
    var approverEmail:String?,
    var approverUsername:String?,
    var uploaderId:String?,
    var phoneNumber:String?,
    var nationalId:String?,
    var sentenceId: String?,
    var actualSentence:String?,
    var languageId:String?,
    var languageName:String?,
    var businessId: String?,
    var genderState: String?,
    var mediaEntityId:String?,
    var mediaState: MediaAcceptanceState =  MediaAcceptanceState.PENDING,
    var paymentState: PaymentState = PaymentState.UNPAID
):AuditMetadata(){
    var reviewDate:LocalDateTime? = null
    var paymentDate:LocalDateTime? = null
}
