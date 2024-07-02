package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaEntityRepository :MongoRepository<MediaEntity,String>{

    fun findAllByBusinessId(businessId:String):List<MediaEntity>

    fun findAllBySentenceId(sentenceId:String):List<MediaEntity>

    fun findAllByCreatedByUser(userEmail:String):List<MediaEntity>

    fun findAllByAccepted(boolean: Boolean):List<MediaEntity>

}