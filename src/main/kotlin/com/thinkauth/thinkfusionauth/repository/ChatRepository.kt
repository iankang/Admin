package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.BotChat
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository :MongoRepository<BotChat,String> {

}