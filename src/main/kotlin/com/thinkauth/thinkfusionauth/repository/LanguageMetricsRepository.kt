package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.LanguageMetricsEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageMetricsRepository:MongoRepository<LanguageMetricsEntity,String> {

    fun findByLanguageId(languageId:String):LanguageMetricsEntity

    fun existsByLanguageId(languageId: String):Boolean
}