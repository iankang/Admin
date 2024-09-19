package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaEntityUserUploadStateRepository:MongoRepository<MediaEntityUserUploadState,String> {

    fun findByMediaEntityId(mediaEntityId:String):MediaEntityUserUploadState

    fun findAllByMediaState(mediaState:MediaAcceptanceState, pageable: Pageable): Page<MediaEntityUserUploadState>


}