package com.thinkauth.thinkfusionauth.utils.async

import com.thinkauth.thinkfusionauth.entities.RelevantLanguages
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.repository.impl.LanguageMetricsImpl
import com.thinkauth.thinkfusionauth.repository.impl.RelevantLanguagesImpl
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.LanguageService
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

            val languagesIds = mediaEntityRepository.findAllByMediaName("VOICE_COLLECTION").filter { it.languageName != "" }.map {

                    LanguageRecordingsResponse(
                        languageName = it.languageName,
                        languageId = it.languageId,
                        sentenceCount = 0L,
                        recordingCount = 0L
                    )

            }.distinct()

            //RELEVANT Languages should be added here as well.
            if(relevantLanguagesImpl.countRelevantLanguages().toInt() != languagesIds.size) {

                val allLangs = languagesIds.filter{ it.languageName != ""  }.map { languageRecordingsResponse: LanguageRecordingsResponse ->
                    logger.info("languages don't tally, delete first")
                    val language = languageService.getLanguageByLanguageId(languageRecordingsResponse.languageId!!)
                    logger.info("language instance: ${language}")
                    RelevantLanguages(
                        languageName = language.languageName,
                        languageId = languageRecordingsResponse.languageId,
                        classification = language.classification,
                        code = language.code,
                        country = language.country
                    )
                }
                relevantLanguagesImpl.deleteAllItems()
                relevantLanguagesImpl.addAllRelevantLanguages(allLangs)
            }
            languagesIds.forEach { languageResp: LanguageRecordingsResponse? ->

                languageResp?.sentenceCount =
                    audioCollectionService.getCountOfAllAudioCollectionByLanguageId(languageResp?.languageId!!) ?: 0L
                languageResp.recordingCount = mediaEntityRepository.countAllByLanguageIdAndMediaName(
                    languageResp.languageId!!,
                    "VOICE_COLLECTION"
                ) ?: 0L
//            val language = languageService.getLanguageByLanguageId(languageId)

                if (languageMetricsImpl.existsByLanguageId(languageResp.languageId!!)) {
                    logger.info("exists and deleting")
                    languageMetricsImpl.removeExistingMetric(languageResp.languageId!!)
                }
                languageMetricsImpl.createItem(languageResp.toLanguageMetricsTbl())
            }

        }catch (e:Exception){
            logger.error("MediaEntityLanguageMetricsAggregationUtil: ${e.stackTrace}")
        }
        return null
    }
}