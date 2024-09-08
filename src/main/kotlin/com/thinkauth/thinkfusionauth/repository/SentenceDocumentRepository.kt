package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceDocumentEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface SentenceDocumentRepository:MongoRepository<SentenceDocumentEntity,String> {
}