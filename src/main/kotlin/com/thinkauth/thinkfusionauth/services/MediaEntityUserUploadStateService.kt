package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.MediaEntityUserUploadStateImpl
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MediaEntityUserUploadStateService(
    private val mediaEntityUserUploadStateImpl: MediaEntityUserUploadStateImpl
) {

    fun addMediaEntityUploadState(mediaEntity: MediaEntity): MediaEntityUserUploadState {
        return mediaEntityUserUploadStateImpl.createItem(item = mediaEntity.toMediaEntityUserUpload())
    }

    fun acceptMediaEntityUserUploadState(
        mediaEntityId:String,
        acceptedCount:Long,
        rejectedCount:Long,
    ): MediaEntityUserUploadState? {
        val mediaEntityUserUploadState = mediaEntityUserUploadStateImpl.getByMediaItemId(mediaEntityId)
        mediaEntityUserUploadState.mediaState = MediaAcceptanceState.ACCEPTED
        mediaEntityUserUploadState.acceptedCount = acceptedCount
        mediaEntityUserUploadState.rejectedCount = rejectedCount
        return mediaEntityUserUploadStateImpl.createItem(mediaEntityUserUploadState)
    }

    fun rejectMediaEntityUserUploadState(
        mediaEntityId:String,
        acceptedCount:Long,
        rejectedCount:Long,
    ): MediaEntityUserUploadState? {
        val mediaEntityUserUploadState = mediaEntityUserUploadStateImpl.getByMediaItemId(mediaEntityId)
        mediaEntityUserUploadState.mediaState = MediaAcceptanceState.REJECTED
        mediaEntityUserUploadState.acceptedCount = acceptedCount
        mediaEntityUserUploadState.rejectedCount = rejectedCount
        return mediaEntityUserUploadStateImpl.createItem(mediaEntityUserUploadState)
    }

    fun makePayment(
        mediaEntityId:String
    ): MediaEntityUserUploadState? {
        val mediaEntityUserUploadState = mediaEntityUserUploadStateImpl.getByMediaItemId(mediaEntityId)
        mediaEntityUserUploadState.paymentDate = LocalDateTime.now()
        mediaEntityUserUploadState.paymentState = PaymentState.PAID
        return mediaEntityUserUploadStateImpl.updateItem(mediaEntityUserUploadState.id!!, mediaEntityUserUploadState)
    }

    fun getAllMediaEntityUploadState(
        page:Int,
        size:Int
    ): PagedResponse<List<MediaEntityUserUploadState>> {
        return mediaEntityUserUploadStateImpl.findEverythingPaged(page, size)
    }

    fun getAllByLanguageIdAndMediaAcceptanceAndPaymentState(
        languageId: String,
        mediaAcceptanceState: MediaAcceptanceState?,
        paymentState: PaymentState?,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserUploadState>> {
        return mediaEntityUserUploadStateImpl.getByLanguageIdAcceptanceStatePaymentState(languageId, mediaAcceptanceState, paymentState, page, size)
    }

    fun getByUserUploadMediaEntityId(
        mediaEntityId: String
    ): MediaEntityUserUploadState {
        return mediaEntityUserUploadStateImpl.getByMediaItemId(mediaEntityId)
    }

    fun getAllByUploadDate(
        uploadDate:LocalDateTime,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<MediaEntityUserUploadState>> {
        return mediaEntityUserUploadStateImpl.getByUploadDate(uploadDate, page, size)
    }

    fun getAllByUploadDateAndMediaState(
        uploadDate:LocalDateTime,
        mediaAcceptanceState: MediaAcceptanceState?,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<MediaEntityUserUploadState>> {
        return mediaEntityUserUploadStateImpl.getByUploadDateAndMediaState(uploadDate,mediaAcceptanceState, page, size)
    }

    fun getAllByUploadDateAndPaymentState(
        uploadDate:LocalDateTime,
        paymentState: PaymentState?,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<MediaEntityUserUploadState>> {
        return mediaEntityUserUploadStateImpl.getByUploadDateAndPaymentState(
            uploadDate, paymentState, page, size
        )
    }
}