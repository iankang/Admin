package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SentenceEntityRepository : MongoRepository<SentenceEntitie, String> {
    fun existsBySentence(sentence: String): Boolean

    fun findAllByLanguageId(languageId: String, pageable: Pageable): Page<SentenceEntitie>

    fun countAudioCollectionsByLanguageId(languageId: String): Long?

    fun findAllByBusinessId(businessId:String):List<SentenceEntitie>

    @Query("{  'needUploads': true }")
    fun findSentencesNotIn(pageable: Pageable): Page<SentenceEntitie>

    @Query("{'needUploads': ?0, 'language.id': ?1 }")
    fun findAllSentencesByNeedUploadsAndLanguageId(needsUpload:Boolean,languageId: String,pageable: Pageable):Page<SentenceEntitie>

    @Query("{'language.id': ?0 , 'needUploads': true}")
    fun findSentencesNotInAndLanguageId(languageId: String, pageable: Pageable): Page<SentenceEntitie>

    fun deleteAllByLanguageId(languageId: String)

    @Query("{'language.id': ?0 , 'createdDate' : { \$gte: ?1, \$lte: ?2 } }")
    fun findAllSentencesByCreatedDateRangeAndLanguageId(languageId: String,createdDateStart:LocalDateTime, createdDateEnd:LocalDateTime,pageable: Pageable):Page<SentenceEntitie>

    fun findAllByLanguageIdAndCreatedDateBetween(languageId: String,createdDateStart:LocalDateTime, createdDateEnd:LocalDateTime,pageable: Pageable):Page<SentenceEntitie>

    fun deleteAllByLanguageIdAndCreatedDateBetween(languageId: String,createdDateStart:LocalDateTime, createdDateEnd:LocalDateTime):Long

    @Query("{'dialect.id': ?0 }")
    fun findAllByDialectId(dialectId:String, pageable: Pageable):Page<SentenceEntitie>

    fun countAllByDialectId(dialectId: String):Long

}