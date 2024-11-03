package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MediaEntityUserApprovalStateRepository:MongoRepository<MediaEntityUserApprovalState, String> {

    fun existsByApproverEmailAndMediaEntityId(approverEmail:String,mediaEntityId:String):Boolean

    fun findAllByMediaEntityId(mediaEntityId: String, pageable: Pageable): Page<MediaEntityUserApprovalState>

    fun findAllByApproverEmailAndPaymentState(approverEmail: String,paymentState: PaymentState,pageable: Pageable):Page<MediaEntityUserApprovalState>

    fun findByMediaEntityIdAndApproverEmail(mediaEntityId: String, approverEmail: String):MediaEntityUserApprovalState

    fun countAllByApproverEmailAndPaymentState(approverEmail: String,paymentState: PaymentState):Long

    fun countAllByApproverEmailAndMediaState(approverEmail: String,mediaState:MediaAcceptanceState):Long

    fun countAllByApproverEmail(approverEmail: String):Long

    fun findAllByReviewDateAndPaymentStateOrderByReviewDateDesc(reviewDate:LocalDateTime, paymentState: PaymentState,pageable: Pageable):Page<MediaEntityUserApprovalState>

    fun findAllByReviewDate(reviewDate:LocalDateTime,pageable: Pageable):Page<MediaEntityUserApprovalState>

    @Query("{ 'reviewDate' : { \$gte: ?0, \$lte: ?1 } }")
    fun findAllByReviewDateStartAndReviewDateEnd(reviewDateStart:LocalDateTime, reviewDaeEnd:LocalDateTime):List<MediaEntityUserApprovalState>

    @Query("{ 'reviewDate' : { \$gte: ?0} }")
    fun findAllByReviewDateStart(reviewDateStart:LocalDateTime):List<MediaEntityUserApprovalState>

    @Query("{ 'paymentDate' : { \$gte: ?0, \$lte: ?1 } }")
    fun findAllByPaymentDateStartAndPaymentDateEnd(paymentDateStart:LocalDateTime, paymentDateEnd:LocalDateTime):List<MediaEntityUserApprovalState>

    @Query("{ 'paymentDate' : { \$gte: ?0} }")
    fun findAllByPaymentDateStart(paymentDateStart:LocalDateTime):List<MediaEntityUserApprovalState>

    fun countByMediaEntityIdAndMediaState(mediaEntityId: String, mediaState:MediaAcceptanceState):Long

    fun countByMediaEntityId(mediaEntityId: String):Long
}