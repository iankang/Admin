package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.Message
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository:MongoRepository<Message,String> {

    fun findAllByConversationId(conversationId:String):List<Message>

    fun countAllByConversationId(conversationId: String):Long
}