package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaEntityUserApprovalStateRepository:MongoRepository<MediaEntityUserApprovalState, String> {

    fun existsByApproverEmailAndMediaEntityId(approverEmail:String,mediaEntityId:String):Boolean

    fun findAllByMediaEntityId(mediaEntityId: String, pageable: Pageable): Page<MediaEntityUserApprovalState>

    fun findByMediaEntityIdAndApproverEmail(mediaEntityId: String, approverEmail: String):MediaEntityUserApprovalState

    fun countByMediaEntityIdAndMediaState(mediaEntityId: String, mediaState:MediaAcceptanceState):Long
}