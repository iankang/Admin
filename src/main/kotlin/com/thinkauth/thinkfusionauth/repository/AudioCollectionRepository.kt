package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.AudioCollection
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioCollectionRepository : MongoRepository<AudioCollection, String> {
    fun existsBySentence(sentence: String): Boolean

    fun findAllByLanguageId(languageId: String): List<AudioCollection>
    fun countAudioCollectionsByLanguageIdAndAudioIsNull(languageId: String): Long?

    fun findAudioCollectionsByLanguageIdAndAudioIsNull(languageId: String):List<AudioCollection>
    fun findAllByAudioIsNullAndLanguageId(languageId: String):List<AudioCollection>

    fun findAllByLanguageIdAndAudio(languageId: String, audioPath:String? = null):List<AudioCollection>

    fun countAudioCollectionsByLanguageIdAndAudioIsNotNull(languageId: String): Long?
    fun findAudioCollectionsByLanguageIdAndAudioIsNotNull(languageId: String):List<AudioCollection>

    fun countAudioCollectionsByAudioIsNotNull(): Long?
    fun countAudioCollectionsByLanguageId(languageId: String): Long?

}