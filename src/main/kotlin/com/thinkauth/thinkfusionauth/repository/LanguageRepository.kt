package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.Language
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageRepository:MongoRepository<Language,String> {

    fun existsByLanguageName(languageName:String):Boolean

    fun existsByCountry(country: String):Boolean

    fun findByLanguageName(languageName: String):List<Language?>

    fun findAllByCountry(country:String):List<Language>

}