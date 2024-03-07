package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MediaEntityService(
    private val mediaEntityRepository: MediaEntityRepository
) {

    fun saveMediaEntity(mediaEntity: MediaEntity): MediaEntity {
        return mediaEntityRepository.save(mediaEntity)
    }

    fun fetchAllMediaEntityPaged(
        page:Int = 0,
        size:Int= 10
    ): PagedResponse<List<MediaEntity>> {
        val paging = PageRequest.of(page,size)
        val content = mediaEntityRepository.findAll(paging)
        return PagedResponse<List<MediaEntity>>(
            content.content,
            content.number,
            content.totalElements,
            content.totalPages
        )
    }
    fun fetchMediaEntityById(id:String): MediaEntity {
        return mediaEntityRepository.findById(id).get()
    }

    fun fetchAllMediaEntityByUser(email:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByCreatedByUser(email)
    }

    fun fetchAllMediaEntityByBusinessId(businessId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByBusinessId(businessId)
    }

    fun fetchAllMediaEntityBySentenceId(sentenceId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllBySentenceId(sentenceId)
    }
}