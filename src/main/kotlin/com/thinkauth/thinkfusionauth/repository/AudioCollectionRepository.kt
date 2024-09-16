package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioCollectionRepository : MongoRepository<SentenceEntitie, String> {
    fun existsBySentence(sentence: String): Boolean

    fun findAllByLanguageId(languageId: String, pageable: Pageable): Page<SentenceEntitie>

    fun countAudioCollectionsByLanguageId(languageId: String): Long?

    fun findAllByBusinessId(businessId:String):List<SentenceEntitie>

}