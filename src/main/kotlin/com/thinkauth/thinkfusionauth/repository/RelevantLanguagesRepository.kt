package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.RelevantLanguages
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RelevantLanguagesRepository:MongoRepository<RelevantLanguages,String> {

    fun findByLanguageName(languageName:String):RelevantLanguages
}