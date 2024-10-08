package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.Conversation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository:MongoRepository<Conversation,String> {

    fun findByUserEmailAndBotInformationId(userEmail:String, botInformationId:String):Conversation

    fun findAllByUserEmail(userEmail: String):List<Conversation>

    fun existsByUserEmail(userEmail: String):Boolean

    fun deleteAllByUserEmail(userEmail: String)

}