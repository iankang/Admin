package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MediaEntityUserUploadStateRepository:MongoRepository<MediaEntityUserUploadState,String> {

    fun findByMediaEntityId(mediaEntityId:String):MediaEntityUserUploadState

    fun findAllByMediaState(mediaState:MediaAcceptanceState, pageable: Pageable): Page<MediaEntityUserUploadState>
    fun findAllByPaymentState(paymentState: PaymentState, pageable: Pageable): Page<MediaEntityUserUploadState>
    fun findAllByLanguageId(languageId:String, pageable: Pageable): Page<MediaEntityUserUploadState>
    fun findAllByLanguageIdAndMediaState(languageId:String, mediaState:MediaAcceptanceState, pageable: Pageable): Page<MediaEntityUserUploadState>
    fun findAllByLanguageIdAndPaymentState(languageId:String, paymentState: PaymentState, pageable: Pageable): Page<MediaEntityUserUploadState>
    fun findAllByLanguageIdAndPaymentStateAndMediaState(languageId:String, paymentState: PaymentState,mediaState:MediaAcceptanceState, pageable: Pageable): Page<MediaEntityUserUploadState>

    fun findAllByUploadDate(uploadDate:LocalDateTime, pageable: Pageable):Page<MediaEntityUserUploadState>
    fun findAllByUploadDateAndMediaState(uploadDate:LocalDateTime, mediaState: MediaAcceptanceState, pageable: Pageable):Page<MediaEntityUserUploadState>
    fun findAllByUploadDateAndPaymentState(uploadDate:LocalDateTime, paymentState: PaymentState, pageable: Pageable):Page<MediaEntityUserUploadState>
    @Query("{ 'paymentDate' : { \$gte: ?0} }")
    fun findAllGreaterThanPaymentDate(paymentDateStart:LocalDateTime):List<MediaEntityUserUploadState>
    @Query("{ 'paymentDate' : { \$gte: ?0, \$lte: ?1 } }")
    fun findAllByPaymentDateRange(paymentDateStart:LocalDateTime, paymentDateEnd:LocalDateTime):List<MediaEntityUserUploadState>
    @Query("{ 'uploadDate' : { \$gte: ?0} }")
    fun findAllGreaterThanUploadDate(uploadDateStart:LocalDateTime):List<MediaEntityUserUploadState>
    @Query("{ 'uploadDate' : { \$gte: ?0, \$lte: ?1 } }")
    fun findAllByUploadDateRange(uploadDateStart:LocalDateTime, uploadDateEnd:LocalDateTime):List<MediaEntityUserUploadState>
}