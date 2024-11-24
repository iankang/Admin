package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageHoursRepository:MongoRepository<LanguageHoursEntity,String> {

    fun findByLanguageId(languageId:String):LanguageHoursEntity

    fun deleteByLanguageId(languageId: String)
}