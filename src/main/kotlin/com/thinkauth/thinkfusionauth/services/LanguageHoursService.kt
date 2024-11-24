package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.repository.impl.LanguageHoursImpl
import com.thinkauth.thinkfusionauth.repository.impl.RelevantLanguagesImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LanguageHoursService(
    private val languageHoursImpl: LanguageHoursImpl,
    private val relevantLanguagesImpl: RelevantLanguagesImpl,
    private val mediaEntityService: MediaEntityService
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    @Scheduled(cron =  "0 0/5 * * * *")
    @Async
    fun setAllDurations(){
        val languages = relevantLanguagesImpl.getAllRelevantLanguages()
        val langHours = languages.map { relevantLanguages ->
            logger.info("relevantLanguage: ${relevantLanguages}")
            val mediaEnt = mediaEntityService.fetchAllMediaEntitiesByLanguageId(relevantLanguages.languageId!!)
            val totalCount = mediaEnt.size
            val totalDuration = mediaEnt.sumOf { it.duration?.toDouble() ?: 0.0 }
            val acceptedCount = mediaEnt.count { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name }
            val acceptedDuration  = mediaEnt.filter { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }
            val rejectedCount = mediaEnt.count { it.mediaState.name == MediaAcceptanceState.REJECTED.name }
            val rejectedDuration  = mediaEnt.filter { it.mediaState.name == MediaAcceptanceState.REJECTED.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }
            val pendingCount = mediaEnt.count { it.mediaState.name == MediaAcceptanceState.PENDING.name }
            val pendingDuration  = mediaEnt.filter { it.mediaState.name == MediaAcceptanceState.PENDING.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }

            val langHour=LanguageHoursEntity(
                languageName = relevantLanguages.languageName,
                languageId = relevantLanguages.languageId,
                totalDuration = totalDuration,
                totalCount = totalCount,
                acceptedCount = acceptedCount,
                acceptedDuration = acceptedDuration,
                rejectedCount = rejectedCount,
                rejectedDuration = rejectedDuration,
                pendingCount = pendingCount,
                pendingDuration = pendingDuration
            )
            languageHoursImpl.deleteByLanguageId(relevantLanguages.languageId!!)
            languageHoursImpl.createItem(langHour)
        }

    }

    fun getAllHourDurations(): MutableList<LanguageHoursEntity> {
        return languageHoursImpl.getAll()
    }
}