package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceDocumentEntity
import com.thinkauth.thinkfusionauth.entities.SentenceDocumentState
import com.thinkauth.thinkfusionauth.repository.impl.SentenceDocumentImpl
import org.springframework.data.mongodb.repository.MongoRepository

interface SentenceDocumentRepository:MongoRepository<SentenceDocumentEntity,String> {

    fun findByDocumentUploadId(documentUploadId:String):SentenceDocumentEntity
}