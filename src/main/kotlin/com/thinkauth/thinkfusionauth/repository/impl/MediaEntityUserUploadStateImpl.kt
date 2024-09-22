package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityUserUploadStateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
data class MediaEntityUserUploadStateImpl(
    private val mediaEntityUserUploadStateRepository: MediaEntityUserUploadStateRepository
):DataOperations<MediaEntityUserUploadState>{
    override fun itemExistsById(id: String): Boolean {
        return mediaEntityUserUploadStateRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<MediaEntityUserUploadState>> {
        val paged = PageRequest.of(page, size)
        val section = mediaEntityUserUploadStateRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): MediaEntityUserUploadState {
       return mediaEntityUserUploadStateRepository.findById(id).orElseThrow {
           ResourceNotFoundException("media entity with id: ${id} not found")
       }
    }

    override fun deleteItemById(id: String) {
        mediaEntityUserUploadStateRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        mediaEntityUserUploadStateRepository.deleteAll()
    }

    override fun updateItem(id: String, item: MediaEntityUserUploadState): MediaEntityUserUploadState? {
       val mediaEntityUserUploadState = getItemById(id)
        mediaEntityUserUploadState.owner = item.owner
        mediaEntityUserUploadState.phoneNumber = item.phoneNumber
        mediaEntityUserUploadState.nationalId = item.nationalId
        mediaEntityUserUploadState.sentenceId = item.sentenceId
        mediaEntityUserUploadState.actualSentence = item.actualSentence
        mediaEntityUserUploadState.languageId = item.languageId
        mediaEntityUserUploadState.languageName = item.languageName
        mediaEntityUserUploadState.businessId = item.businessId
        mediaEntityUserUploadState.genderState = item.genderState
        mediaEntityUserUploadState.mediaEntityId = item.mediaEntityId
        mediaEntityUserUploadState.mediaState = item.mediaState
        mediaEntityUserUploadState.paymentState = item.paymentState
        return createItem(mediaEntityUserUploadState)
    }

    override fun createItem(item: MediaEntityUserUploadState): MediaEntityUserUploadState {
       return mediaEntityUserUploadStateRepository.save(item)
    }


    fun getByMediaItemId(mediaItemId:String): MediaEntityUserUploadState {
        return mediaEntityUserUploadStateRepository.findByMediaEntityId(mediaItemId)
    }

    fun getByLanguageIdAcceptanceStatePaymentState(
        languageId: String,
        mediaAcceptanceState: MediaAcceptanceState?,
        paymentState: PaymentState?,
        page: Int,
        size: Int
    ): PagedResponse<MutableList<MediaEntityUserUploadState>> {
        val paged = PageRequest.of(page, size)
        val section: Page<MediaEntityUserUploadState> = when {
            mediaAcceptanceState == null && paymentState == null -> {
                mediaEntityUserUploadStateRepository.findAllByLanguageId(languageId, paged)
            }
            paymentState != null && mediaAcceptanceState != null -> {
                mediaEntityUserUploadStateRepository.findAllByLanguageIdAndPaymentStateAndMediaState(
                    languageId, paymentState, mediaAcceptanceState, paged
                )
            }
            paymentState != null && mediaAcceptanceState == null -> {
                mediaEntityUserUploadStateRepository.findAllByLanguageIdAndPaymentState(
                    languageId, paymentState, paged
                )
            }
            paymentState == null && mediaAcceptanceState != null -> {
                mediaEntityUserUploadStateRepository.findAllByLanguageIdAndMediaState(
                    languageId, mediaAcceptanceState, paged
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid combination of states")
            }
        }

        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }
}
