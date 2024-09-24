package com.thinkauth.thinkfusionauth.repository.impl

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
    override fun itemExistsById(id: String): Boolean {
        return mediaEntityUserApprovalStateRepository.existsById(id)
    }

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

    override fun getItemById(id: String): MediaEntityUserApprovalState {
        return mediaEntityUserApprovalStateRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        mediaEntityUserApprovalStateRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        mediaEntityUserApprovalStateRepository.deleteAll()
    }

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

    override fun createItem(item: MediaEntityUserApprovalState): MediaEntityUserApprovalState {
        return mediaEntityUserApprovalStateRepository.save(item)
    }

    fun countByMediaEntityIdAndMediaState(
        mediaEntityId:String,
        mediaAcceptanceState: MediaAcceptanceState
    ): Long {
        return mediaEntityUserApprovalStateRepository.countByMediaEntityIdAndMediaState(mediaEntityId,mediaAcceptanceState)
    }

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

    fun checkIfApprovalAlreadyAdded(
        approverEmail:String,
        mediaEntityId:String
    ): Boolean {
        return mediaEntityUserApprovalStateRepository.existsByApproverEmailAndMediaEntityId(approverEmail, mediaEntityId)
    }

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

    fun getAllApprovalsByApproverEmailAndPaymentState(
        approverEmail:String,
        paymentState: PaymentState,
        page: Int,
        size: Int
    ): Page<MediaEntityUserApprovalState> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        return mediaEntityUserApprovalStateRepository.findAllByApproverEmailAndPaymentState(approverEmail,paymentState,paging)
    }

    fun getByReviewDateAndPaymentState(
        reviewDate:LocalDateTime,
        paymentState: PaymentState,
        page: Int,
        size: Int
    ): Page<MediaEntityUserApprovalState> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        return mediaEntityUserApprovalStateRepository.findAllByReviewDateAndPaymentStateOrderByReviewDateDesc( reviewDate, paymentState, paging)
    }

    fun getByReviewDate(
        reviewDate: LocalDateTime,
        page: Int,
        size: Int
    ): Page<MediaEntityUserApprovalState> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        return mediaEntityUserApprovalStateRepository.findAllByReviewDate(reviewDate,paging)
    }
}