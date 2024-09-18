package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<UserEntity, String> {

    fun findByEmail(email:String):UserEntity
    fun findAllByEmail(email:String):List<UserEntity>

    fun existsByEmail(email: String):Boolean

    fun existsByUsername(username:String):Boolean
    fun countByEmail(email: String):Long
    fun countByUsername(username: String):Long

    fun deleteAllByEmail(email:String)
}