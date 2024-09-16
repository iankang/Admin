package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioCollectionRepository : MongoRepository<SentenceEntity, String> {
    fun existsBySentence(sentence: String): Boolean

    fun findAllByLanguageId(languageId: String, pageable: Pageable): Page<SentenceEntity>

    fun countAudioCollectionsByLanguageId(languageId: String): Long?

    fun findAllByBusinessId(businessId:String):List<SentenceEntity>

}