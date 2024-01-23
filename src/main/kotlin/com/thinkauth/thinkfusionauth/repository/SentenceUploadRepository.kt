package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceUploadEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

interface SentenceUploadRepository:MongoRepository<SentenceUploadEntity,String> {
}