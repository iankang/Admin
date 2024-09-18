package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceUserIgnore
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SentenceIgnoreRepository:MongoRepository<SentenceUserIgnore,String>{

    fun findAllByUserId(userId:String):List<SentenceUserIgnore>

    fun findBySentenceIdAndUserId(sentenceId:String, userId: String):SentenceUserIgnore

    fun existsBySentenceIdAndUserId(sentenceId:String, userId: String):Boolean

    fun countByUserId(userId: String):Long
}