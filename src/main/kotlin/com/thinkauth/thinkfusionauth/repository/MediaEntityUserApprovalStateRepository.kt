package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MediaEntityUserApprovalStateRepository:MongoRepository<MediaEntityUserApprovalState, String> {

    fun existsByApproverEmailAndMediaEntityId(approverEmail:String,mediaEntityId:String):Boolean

    fun findAllByMediaEntityId(mediaEntityId: String, pageable: Pageable): Page<MediaEntityUserApprovalState>

    fun findAllByApproverEmailAndPaymentState(approverEmail: String,paymentState: PaymentState,pageable: Pageable):Page<MediaEntityUserApprovalState>

    fun findByMediaEntityIdAndApproverEmail(mediaEntityId: String, approverEmail: String):MediaEntityUserApprovalState

    fun findAllByReviewDateAndPaymentStateOrderByReviewDateDesc(reviewDate:LocalDateTime, paymentState: PaymentState,pageable: Pageable):Page<MediaEntityUserApprovalState>

    fun findAllByReviewDate(reviewDate:LocalDateTime,pageable: Pageable):Page<MediaEntityUserApprovalState>

    fun countByMediaEntityIdAndMediaState(mediaEntityId: String, mediaState:MediaAcceptanceState):Long

    fun countByMediaEntityId(mediaEntityId: String):Long
}