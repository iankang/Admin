package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.PromptEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PromptRepository: MongoRepository<PromptEntity,String> {
}