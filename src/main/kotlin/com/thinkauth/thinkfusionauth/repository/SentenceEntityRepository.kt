package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SentenceEntityRepository : MongoRepository<SentenceEntitie, String> {
    fun existsBySentence(sentence: String): Boolean

    fun findAllByLanguageId(languageId: String, pageable: Pageable): Page<SentenceEntitie>

    fun countAudioCollectionsByLanguageId(languageId: String): Long?

    fun findAllByBusinessId(businessId:String):List<SentenceEntitie>

    @Query("{  'needUploads': true }")
    fun findSentencesNotIn(pageable: Pageable): Page<SentenceEntitie>

    @Query("{ 'id': { '\$nin': ?0 }, 'language.id': ?1 , 'needUploads': true}")
    fun findSentencesNotInAndLanguageId(sentenceIds: List<String>,languageId: String, pageable: Pageable): Page<SentenceEntitie>

    fun deleteAllByLanguageId(languageId: String)



}