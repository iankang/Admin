package com.thinkauth.thinkfusionauth.utils.async

import com.thinkauth.thinkfusionauth.entities.LanguageMetricsEntity
import com.thinkauth.thinkfusionauth.entities.RelevantLanguages
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.repository.impl.LanguageMetricsImpl
import com.thinkauth.thinkfusionauth.repository.impl.RelevantLanguagesImpl
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.LanguageService
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MediaEntityLanguageMetricsAggregationUtil(
    private val relevantLanguagesImpl: RelevantLanguagesImpl,
    private val languageMetricsImpl: LanguageMetricsImpl,
    private val languageService: LanguageService,
    private val mediaEntityRepository: MediaEntityRepository,
    private val audioCollectionService: AudioCollectionService,
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Async
    fun countAllByLanguages(): MutableList<LanguageRecordingsResponse>?{
        try {
            val mediaEntitiesLanguages = languageService.aggregateMediaEntitiesLanguages()
                .filter { it.id.languageId != null }.distinct()
            val languageMetrics = mediaEntitiesLanguages.map {
                LanguageMetricsEntity(
                    languageId = it.id.languageId,
                    languageName = it.id.languageName,
                    recordingCount = it.recordingCount?.toLong(),
                    sentenceCount =  audioCollectionService.getCountOfAllAudioCollectionByLanguageId(it.id.languageId!!) ?: 0L
                )
            }
            if(languageMetrics.isNotEmpty()){
                logger.info("language metrics not empty")
                languageMetricsImpl.deleteAllItems()
                languageMetricsImpl.addAllMetrics(languageMetrics)
            }

        }catch (e:Exception){
            logger.error("MediaEntityLanguageMetricsAggregationUtil: ${e.stackTrace}")
        }
        return null
    }
}