package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityUserApprovalStateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MediaEntityUserApprovalStateImpl(
    private val mediaEntityUserApprovalStateRepository: MediaEntityUserApprovalStateRepository
):DataOperations<MediaEntityUserApprovalState> {
    @TrackExecutionTime
    override fun itemExistsById(id: String): Boolean {
        return mediaEntityUserApprovalStateRepository.existsById(id)
    }

    @TrackExecutionTime
    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<MediaEntityUserApprovalState>> {
        val paged = PageRequest.of(page, size)
        val section = mediaEntityUserApprovalStateRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    @TrackExecutionTime
    override fun getItemById(id: String): MediaEntityUserApprovalState {
        return mediaEntityUserApprovalStateRepository.findById(id).get()
    }

    @TrackExecutionTime
    override fun deleteItemById(id: String) {
        mediaEntityUserApprovalStateRepository.deleteById(id)
    }

    @TrackExecutionTime
    override fun deleteAllItems() {
        mediaEntityUserApprovalStateRepository.deleteAll()
    }

    @TrackExecutionTime
    override fun updateItem(id: String, item: MediaEntityUserApprovalState): MediaEntityUserApprovalState? {
        val mediaEntityUserApprovalState = getItemById(id)
        mediaEntityUserApprovalState.mediaEntityId = item.mediaEntityId
        mediaEntityUserApprovalState.mediaState = item.mediaState
        mediaEntityUserApprovalState.paymentState = item.paymentState
        mediaEntityUserApprovalState.genderState = item.genderState
        mediaEntityUserApprovalState.owner = item.owner
        mediaEntityUserApprovalState.actualSentence = item.actualSentence
        mediaEntityUserApprovalState.businessId = item.businessId
        mediaEntityUserApprovalState.languageId = item.languageId
        mediaEntityUserApprovalState.languageName = item.languageName
        mediaEntityUserApprovalState.nationalId = item.nationalId
        mediaEntityUserApprovalState.paymentDate = item.paymentDate
        mediaEntityUserApprovalState.reviewDate = item.reviewDate
        mediaEntityUserApprovalState.phoneNumber = item.phoneNumber
        mediaEntityUserApprovalState.sentenceId = item.sentenceId
        mediaEntityUserApprovalState.uploaderId = item.uploaderId
        mediaEntityUserApprovalState.approverUsername = item.owner.username
        mediaEntityUserApprovalState.approverEmail = item.owner.email
        return createItem(mediaEntityUserApprovalState)
    }

    @TrackExecutionTime
    override fun createItem(item: MediaEntityUserApprovalState): MediaEntityUserApprovalState {
        return mediaEntityUserApprovalStateRepository.save(item)
    }

    @TrackExecutionTime
    fun countByMediaEntityIdAndMediaState(
        mediaEntityId:String,
        mediaAcceptanceState: MediaAcceptanceState
    ): Long {
        return mediaEntityUserApprovalStateRepository.countByMediaEntityIdAndMediaState(mediaEntityId,mediaAcceptanceState)
    }

    @TrackExecutionTime
    fun getAllApprovalsOfSpecificMedia(
        mediaEntityId: String,
        page: Int, size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        val paged = PageRequest.of(page, size)
        val section = mediaEntityUserApprovalStateRepository.findAllByMediaEntityId(mediaEntityId, paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    @TrackExecutionTime
    fun checkIfApprovalAlreadyAdded(
        approverEmail:String,
        mediaEntityId:String
    ): Boolean {
        return mediaEntityUserApprovalStateRepository.existsByApproverEmailAndMediaEntityId(approverEmail, mediaEntityId)
    }

    @TrackExecutionTime
    fun getMediaEntityForAnApprover(
        mediaEntityId: String,
        approverEmail: String
    ): MediaEntityUserApprovalState {
        return mediaEntityUserApprovalStateRepository.findByMediaEntityIdAndApproverEmail(mediaEntityId, approverEmail)
    }

    fun countTheNumberOfReviewsOnAMediaEntity(
        mediaEntityId: String
    ): Long {
        return mediaEntityUserApprovalStateRepository.countByMediaEntityId(mediaEntityId)
    }

    @TrackExecutionTime
    fun getAllApprovalsByApproverEmailAndPaymentState(
        approverEmail:String,
        paymentState: PaymentState,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val approvalState =mediaEntityUserApprovalStateRepository.findAllByApproverEmailAndPaymentState(approverEmail,paymentState,paging)
        return PagedResponse<MutableList<MediaEntityUserApprovalState>>(approvalState.content,approvalState.number, approvalState.totalElements, approvalState.totalPages)

    }

    @TrackExecutionTime
    fun getAllApprovalsByApproverEmailAndAcceptanceState(
        approverEmail:String,
        mediaAcceptanceState: MediaAcceptanceState,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val approvalState = mediaEntityUserApprovalStateRepository.findAllByApproverEmailAndMediaState(approverEmail,mediaAcceptanceState,paging)
        return PagedResponse<MutableList<MediaEntityUserApprovalState>>(approvalState.content,approvalState.number, approvalState.totalElements, approvalState.totalPages)
    }
    @TrackExecutionTime
    fun getByReviewDateAndPaymentState(
        reviewDate:LocalDateTime,
        paymentState: PaymentState,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val approvalState = mediaEntityUserApprovalStateRepository.findAllByReviewDateAndPaymentStateOrderByReviewDateDesc( reviewDate, paymentState, paging)
        return PagedResponse<MutableList<MediaEntityUserApprovalState>>(approvalState.content,approvalState.number, approvalState.totalElements, approvalState.totalPages)
    }

    @TrackExecutionTime
    fun getByReviewDate(
        reviewDate: LocalDateTime,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val approvalState = mediaEntityUserApprovalStateRepository.findAllByReviewDate(reviewDate,paging)
        return PagedResponse<MutableList<MediaEntityUserApprovalState>>(approvalState.content,approvalState.number, approvalState.totalElements, approvalState.totalPages)
    }

    @TrackExecutionTime
    fun getByReviewDateStartAndReviewDateEnd(
        reviewDateStart: LocalDateTime,
        reviewDateEnd: LocalDateTime
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateRepository.findAllByReviewDateStartAndReviewDateEnd(reviewDateStart, reviewDateEnd)
    }

    @TrackExecutionTime
    fun getByReviewDateStart(
        reviewDateStart: LocalDateTime,
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateRepository.findAllByReviewDateStart(reviewDateStart)
    }

    @TrackExecutionTime
    fun getByPaymentDateStartAndPaymentDateEnd(
        paymentDateStart: LocalDateTime,
        paymentDateEnd: LocalDateTime
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateRepository.findAllByPaymentDateStartAndPaymentDateEnd(
            paymentDateStart, paymentDateEnd
        )
    }

    @TrackExecutionTime
    fun getAllByPaymentDateGreaterThanPaymentStart(
        paymentDateStart: LocalDateTime
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateRepository.findAllByPaymentDateStart(paymentDateStart)
    }

    @TrackExecutionTime
    fun getCountByApproverEmail(
        approverEmail: String
    ): Long {
        return mediaEntityUserApprovalStateRepository.countAllByApproverEmail(approverEmail)
    }

    @TrackExecutionTime
    fun getCountByApproverEmailAndMediaAcceptanceState(
        approverEmail: String,
        mediaAcceptanceState: MediaAcceptanceState
    ): Long {
        return mediaEntityUserApprovalStateRepository.countAllByApproverEmailAndMediaState(approverEmail,mediaAcceptanceState)
    }

    @TrackExecutionTime
    fun getCountByApproverEmailAndPaymentState(
        approverEmail: String,
        paymentState: PaymentState
    ): Long {
        return mediaEntityUserApprovalStateRepository.countAllByApproverEmailAndPaymentState(approverEmail,paymentState)
    }

    @TrackExecutionTime
    fun getAllApprovers(): MutableList<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateRepository.findAll()
    }
}