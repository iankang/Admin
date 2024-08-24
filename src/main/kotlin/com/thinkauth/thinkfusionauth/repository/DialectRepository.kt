package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.entities.Language
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DialectRepository: MongoRepository<Dialect, String> {

    fun countAllByLanguageLanguageName(languageName:String):Long

    fun findAllByLanguageLanguageName(languageName:String):List<Dialect>

    fun existsByDialectName(dialectName:String):Boolean
}