package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.BotInformation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BotInformationRepository: MongoRepository<BotInformation, String> {
}