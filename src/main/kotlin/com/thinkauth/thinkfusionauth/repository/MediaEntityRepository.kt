package com.thinkauth.thinkfusionauth.repository

import com.github.javafaker.Bool
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaEntityRepository :MongoRepository<MediaEntity,String>{

    fun findAllByBusinessId(businessId:String):List<MediaEntity>

    fun findAllBySentenceId(sentenceId:String):List<MediaEntity>

    fun existsBySentenceId(sentenceId: String):Boolean

    fun findAllByLanguageId(languageId: String,pageable: Pageable):Page<MediaEntity>

    fun findAllByLanguageId(languageId: String):List<MediaEntity>

    fun findAllByCreatedByUser(userEmail:String):List<MediaEntity>

    fun findAllByOwnerEmail(userEmail: String):List<MediaEntity>

    fun countAllByUsernameAndMediaName(userEmail:String,mediaName:String):Long

    fun countAllByUsernameAndMediaNameAndLanguageId(userEmail:String,mediaName:String,languageId:String):Long

    fun findAllByUsernameAndMediaName( userEmail:String, mediaName:String,pageable: Pageable): Page<MediaEntity>
    fun findAllByUsernameAndMediaNameAndLanguageId( username:String, mediaName:String,languageId: String,pageable: Pageable): Page<MediaEntity>

//    fun findAllByAccepted(boolean: Boolean):List<MediaEntity>

    fun findAllByMediaName(mediaName:String):List<MediaEntity>

    fun findAllByMediaState(mediaAcceptanceState: MediaAcceptanceState, pageable: Pageable):Page<MediaEntity>

    fun findAllByMediaStateAndLanguageId(mediaAcceptanceState: MediaAcceptanceState,languageId:String,pageable: Pageable):Page<MediaEntity>

    fun findAllByDialectId(dialectId:String?,pageable: Pageable):Page<MediaEntity>

    fun countAllByDialectId(dialectId: String?):Long

    fun countAllByMediaName(mediaName:String):Long

    fun countAllByMediaStateAndMediaName(mediaAcceptanceState: MediaAcceptanceState, mediaName:String):Long

    fun countByMediaStateAndLanguageId(mediaAcceptanceState: MediaAcceptanceState,languageId:String):Long

    fun countAllByLanguageIdAndMediaName(languageId:String,mediaName:String):Long

    fun countAllByLanguageId(languageId:String):Long

    fun countAllByDuration(duration:Float?):Long

    fun findAllByDuration(duration: Float?):List<MediaEntity>

    fun deleteAllByLanguageId(languageId: String)

}