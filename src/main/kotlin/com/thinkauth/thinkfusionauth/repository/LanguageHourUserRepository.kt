package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.LanguageHourUserEntity
import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageHourUserRepository:MongoRepository<LanguageHourUserEntity,String> {

    fun existsByEmail(email:String):Boolean

    fun findByEmail(email: String):LanguageHourUserEntity?

    fun countByEmail(email: String):Long
}