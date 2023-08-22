package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.repository.LanguageRepository
import org.springframework.stereotype.Service

@Service
class LanguageService(
    private val languageRepository: LanguageRepository,
) {
    @TrackExecutionTime
    fun addLanguage(languageRequest: LanguageRequest): Language {
        return languageRepository.insert(Language(languageName = languageRequest.languageName))
    }
    @TrackExecutionTime
    fun existsByLanguageName(languageRequest: LanguageRequest):Boolean{
        return languageRepository.existsByLanguageName(languageRequest.languageName?: "Swahili")
    }
    @TrackExecutionTime
    fun existsByLanguageId(languageId:String):Boolean{
        return languageRepository.existsById(languageId)
    }
    @TrackExecutionTime
    fun getLanguages():List<Language>{

        return languageRepository.findAll()
    }

    @TrackExecutionTime
    fun getLanguageByLanguageId(languageId: String):Language{
        return languageRepository.findById(languageId).get()
    }

    @TrackExecutionTime
    fun findLanguageByLanguageName(languageName:String):Language?{
        return languageRepository.findByLanguageName(languageName = languageName)
    }
    @TrackExecutionTime
    fun findLanguageCount(): Long {
        return languageRepository.count()
    }
    @TrackExecutionTime
    fun deleteAllLanguages(){
        return languageRepository.deleteAll()
    }
}