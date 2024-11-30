package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.repository.impl.LanguageHoursImpl
import com.thinkauth.thinkfusionauth.repository.impl.RelevantLanguagesImpl
import com.thinkauth.thinkfusionauth.utils.async.MediaEntityLanguageHoursAggregationUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LanguageHoursService(
    private val languageHoursImpl: LanguageHoursImpl,
    private val mediaEntityLanguageHoursAggregationUtil: MediaEntityLanguageHoursAggregationUtil
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    @Scheduled(cron =  "0 0/5 * * * *")
    fun setLanguageHourMetrics(){
        mediaEntityLanguageHoursAggregationUtil.setAllDurations()
    }


    fun getAllHourDurations(): MutableList<LanguageHoursEntity> {
        return languageHoursImpl.getAll()
    }
}