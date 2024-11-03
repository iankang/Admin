package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.models.responses.ApproverCount
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.MediaEntityUserApprovalStateImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class MediaEntityUserApprovalStateService(
    private val mediaEntityUserApprovalStateImpl: MediaEntityUserApprovalStateImpl,
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService,
    private val mediaEntityService: MediaEntityService,
    private val userManagementService: UserManagementService,
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun acceptMediaEntity(
        mediaEntityId: String
    ): MediaEntityUserApprovalState {
        val loggedInUser = userManagementService.fetchLoggedInUserEntity()
        val uploadedMedia = mediaEntityUserUploadStateService.getByUserUploadMediaEntityId(mediaEntityId)
        if (!mediaEntityUserApprovalStateImpl.checkIfApprovalAlreadyAdded(
                loggedInUser.email ?: loggedInUser.username!!, mediaEntityId
            )
        ) {
            val approvalState = MediaEntityUserApprovalState(
                owner = loggedInUser,
                approverEmail = loggedInUser.email,
                approverUsername = loggedInUser.username,
                uploaderId = uploadedMedia.owner.id,
                phoneNumber = loggedInUser.mobilePhone,
                nationalId = loggedInUser.nationalId,
                sentenceId = uploadedMedia.sentenceId,
                actualSentence = uploadedMedia.actualSentence,
                languageId = uploadedMedia.languageId,
                languageName = uploadedMedia.languageName,
                businessId = uploadedMedia.businessId,
                genderState = loggedInUser.genderState,
                mediaEntityId = uploadedMedia.mediaEntityId,
                mediaState = MediaAcceptanceState.ACCEPTED,
                paymentState = uploadedMedia.paymentState
            )
            approvalState.reviewDate = LocalDateTime.now()

            mediaEntityUserApprovalStateImpl.createItem(approvalState)

            mediaEntityUserUploadStateService.acceptMediaEntityUserUploadState(
                mediaEntityId, mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.ACCEPTED
                ), mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.REJECTED
                )
            )
            mediaEntityService.acceptMediaEntity(mediaEntityId)
            return approvalState
        } else {
            val approvalState = mediaEntityUserApprovalStateImpl.getMediaEntityForAnApprover(
                mediaEntityId, loggedInUser.email ?: loggedInUser.username!!
            )
            logger.info("approvalState: " + approvalState)
            approvalState.reviewDate = LocalDateTime.now()
            mediaEntityUserApprovalStateImpl.updateItem(approvalState.id!!, approvalState)
            val acceptedCount = mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                mediaEntityId, MediaAcceptanceState.ACCEPTED
            )
            logger.info("acceptedcount: " + acceptedCount)
            mediaEntityUserUploadStateService.acceptMediaEntityUserUploadState(
                mediaEntityId, acceptedCount, mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.REJECTED
                )
            )
            mediaEntityService.acceptMediaEntity(mediaEntityId)
            return approvalState
        }
    }

    fun rejectMediaEntity(
        mediaEntityId: String
    ): MediaEntityUserApprovalState {
        val loggedInUser = userManagementService.fetchLoggedInUserEntity()
        val uploadedMedia = mediaEntityUserUploadStateService.getByUserUploadMediaEntityId(mediaEntityId)
        if (!mediaEntityUserApprovalStateImpl.checkIfApprovalAlreadyAdded(
                loggedInUser.email ?: loggedInUser.username!!, mediaEntityId
            )
        ) {
            val approvalState = MediaEntityUserApprovalState(
                owner = loggedInUser,
                approverEmail = loggedInUser.email,
                approverUsername = loggedInUser.username,
                uploaderId = uploadedMedia.owner.id,
                phoneNumber = loggedInUser.mobilePhone,
                nationalId = loggedInUser.nationalId,
                sentenceId = uploadedMedia.sentenceId,
                actualSentence = uploadedMedia.actualSentence,
                languageId = uploadedMedia.languageId,
                languageName = uploadedMedia.languageName,
                businessId = uploadedMedia.businessId,
                genderState = loggedInUser.genderState,
                mediaEntityId = uploadedMedia.mediaEntityId,
                mediaState = MediaAcceptanceState.REJECTED,
                paymentState = uploadedMedia.paymentState
            )
            mediaEntityUserApprovalStateImpl.createItem(approvalState)

            mediaEntityUserUploadStateService.rejectMediaEntityUserUploadState(
                mediaEntityId, mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.ACCEPTED
                ), mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.REJECTED
                )
            )
            mediaEntityService.rejectMediaEntity(mediaEntityId)
            return approvalState
        } else {
            val approvalState = mediaEntityUserApprovalStateImpl.getMediaEntityForAnApprover(
                mediaEntityId, loggedInUser.email ?: loggedInUser.username!!
            )
            mediaEntityUserApprovalStateImpl.updateItem(approvalState.id!!, approvalState)
            mediaEntityUserUploadStateService.rejectMediaEntityUserUploadState(
                mediaEntityId, mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.ACCEPTED
                ), mediaEntityUserApprovalStateImpl.countByMediaEntityIdAndMediaState(
                    mediaEntityId, MediaAcceptanceState.REJECTED
                )
            )
            mediaEntityService.rejectMediaEntity(mediaEntityId)
            return approvalState
        }
    }

    fun makePayment(
        mediaEntityId: String, approverEmail: String
    ): MediaEntityUserApprovalState {

        val mediaEntityUserApprovalState =
            mediaEntityUserApprovalStateImpl.getMediaEntityForAnApprover(mediaEntityId, approverEmail)
        mediaEntityUserApprovalState.paymentState = PaymentState.PAID
        mediaEntityUserApprovalState.paymentDate = LocalDateTime.now()
        return mediaEntityUserApprovalStateImpl.createItem(mediaEntityUserApprovalState)
    }

    fun getMediaEntityUserApproval(
        page: Int, size: Int
    ): PagedResponse<List<MediaEntityUserApprovalState>> {
        return mediaEntityUserApprovalStateImpl.findEverythingPaged(page, size)
    }

    fun getAllApprovalsOfSpecificMedia(
        mediaEntityId: String, page: Int, size: Int
    ): PagedResponse<MutableList<MediaEntityUserApprovalState>> {
        return mediaEntityUserApprovalStateImpl.getAllApprovalsOfSpecificMedia(mediaEntityId, page, size)
    }

    fun getAllByApproverEmailAndPaymentState(
        approverEmail: String, paymentState: PaymentState, page: Int, size: Int
    ): Page<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getAllApprovalsByApproverEmailAndPaymentState(
            approverEmail, paymentState, page, size
        )
    }

    fun getAllByReviewDateAndPaymentState(
        reviewDate: LocalDateTime, paymentState: PaymentState, page: Int, size: Int
    ): Page<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getByReviewDateAndPaymentState(reviewDate, paymentState, page, size)
    }

    fun getAllByReviewDate(
        reviewDate: LocalDateTime, page: Int, size: Int
    ): Page<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getByReviewDate(reviewDate, page, size)
    }

    fun getAllByReviewDateRange(
        reviewDateStart: LocalDate,
        reviewDateEnd: LocalDate,
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getByReviewDateStartAndReviewDateEnd(reviewDateStart.atStartOfDay(), reviewDateEnd.atStartOfDay())
    }

    fun getAllReviewGreaterThanDate(
        reviewDateStart: LocalDate,
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getByReviewDateStart(reviewDateStart.atStartOfDay())
    }

    fun getAllByPaymentDateRange(
        paymentDateStart: LocalDate,
        paymentDateEnd: LocalDate,
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getByPaymentDateStartAndPaymentDateEnd(
            paymentDateStart.atStartOfDay(),
            paymentDateEnd.atStartOfDay()
        )
    }

    fun getAllByPaymentDateStart(
        paymentDateStart: LocalDate
    ): List<MediaEntityUserApprovalState> {
        return mediaEntityUserApprovalStateImpl.getAllByPaymentDateGreaterThanPaymentStart(
            paymentDateStart.atStartOfDay()
        )
    }

    fun getCountByApproverEmail(
        approverEmail: String
    ): ApproverCount {
        var mediaEntityCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmail(approverEmail)
        var acceptedCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmailAndMediaAcceptanceState(approverEmail,MediaAcceptanceState.ACCEPTED)
        var rejectedCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmailAndMediaAcceptanceState(approverEmail,MediaAcceptanceState.REJECTED)
        var pendingCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmailAndMediaAcceptanceState(approverEmail,MediaAcceptanceState.PENDING)
        var paidCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmailAndPaymentState(approverEmail,PaymentState.PAID)
        var unpaidCount = mediaEntityUserApprovalStateImpl.getCountByApproverEmailAndPaymentState(approverEmail,PaymentState.UNPAID)

        return ApproverCount(
            mediaEntitiesCount = mediaEntityCount,
            acceptedCount = acceptedCount,
            rejectedCount = rejectedCount,
            paidCount = paidCount,
            unpaidCount = unpaidCount,
            pendingCount = pendingCount
        )
    }
}