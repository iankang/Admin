package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.responses.LanguageScrapeResponse
import com.thinkauth.thinkfusionauth.repository.LanguageRepository
import com.thinkauth.thinkfusionauth.utils.toStandardCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class LanguageService(
    private val languageRepository: LanguageRepository,
    private val scrapingService: ScrapingService
) {

    @TrackExecutionTime
    fun addLanguage(languageRequest: LanguageRequest): Language {
        return languageRepository.insert(Language(languageName = languageRequest.languageName))
    }
    @TrackExecutionTime
    fun addLanguages(languages:MutableList<LanguageScrapeResponse>): MutableList<Language> {

        val langs = languages.map { Language(
            code = it.code,
            languageName = it.Language,
            country = it.Country,
            classification = it.classification
        ) }
        return languageRepository.saveAll(langs)
    }
    @TrackExecutionTime
    fun existsByLanguageName(languageName:String):Boolean{
        return languageRepository.existsByLanguageName(languageName.toStandardCase())
    }
    @TrackExecutionTime
    fun existsByLanguageId(languageId:String):Boolean{
        return languageRepository.existsById(languageId)
    }
    @TrackExecutionTime
    fun existsByCountry(country:String):Boolean{
        return languageRepository.existsByCountry(country)
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
    fun getLanguagesCount(): Long {
        return languageRepository.count()
    }

    @TrackExecutionTime
    fun findLanguageByLanguageName(languageName:String):List<Language?>{
        return languageRepository.findByLanguageName(languageName = languageName.toStandardCase())
    }
    @TrackExecutionTime
    fun findLanguageCount(): Long {
        return languageRepository.count()
    }
    @TrackExecutionTime
    fun deleteAllLanguages(){
        return languageRepository.deleteAll()
    }
    @TrackExecutionTime
    fun fetchLanguagesByCountry(country:String): List<Language> {
        return languageRepository.findAllByCountry(country)
    }

    fun downloadLanguagesFromWikipedia(): MutableList<LanguageScrapeResponse> {
        return scrapingService.fetchLanguages()
    }
    @TrackExecutionTime
    fun deleteByLanguageId(languageId: String){
        languageRepository.deleteById(languageId)
    }
}