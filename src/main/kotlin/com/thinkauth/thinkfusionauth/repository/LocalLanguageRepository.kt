package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.LocalLanguage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalLanguageRepository:MongoRepository<LocalLanguage,String> {

    fun existsByLanguageName(languageName:String):Boolean

    fun findAllByLanguageName(languageName:String):List<LocalLanguage>
}