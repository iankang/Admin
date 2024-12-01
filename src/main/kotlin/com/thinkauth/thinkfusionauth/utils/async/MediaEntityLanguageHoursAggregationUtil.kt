package com.thinkauth.thinkfusionauth.utils.async

import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import com.thinkauth.thinkfusionauth.entities.TotalHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.models.responses.DurationSum
import com.thinkauth.thinkfusionauth.repository.TotalHourRepository
import com.thinkauth.thinkfusionauth.repository.impl.LanguageHoursImpl
import com.thinkauth.thinkfusionauth.repository.impl.RelevantLanguagesImpl
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MediaEntityLanguageHoursAggregationUtil(
    private val mediaEntityService: MediaEntityService,
    private val languageHoursImpl: LanguageHoursImpl,
    private val relevantLanguagesImpl: RelevantLanguagesImpl,
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Async
    fun setAllDurations(){
        val languages = mediaEntityService.aggregateLanguageHoursMediaEntities().filterNot { it.id.languageId == null   }

        val languageStateMap = mutableMapOf<String,LanguageHoursEntity>()
        languages.map { durationsum ->
            logger.info("languageduration: ${durationsum}")
            val totalCount:Long = mediaEntityService.countAllVoiceCollectionsByLanguageId(durationsum.id.languageId!!)
            var totalAccepted:Int? = 0
            var totalAcceptedDuration:Float? = 0.0f
            var totalRejected:Int? = 0
            var totalRejectedDuration:Float? = 0.0f
            var totalPending:Int? = 0
            var totalPendingDuration:Float? = 0.0f


            when(durationsum.id.mediaState){
                MediaAcceptanceState.PENDING -> {
                    totalPending = durationsum.recordingCount
                    totalPendingDuration = durationsum.totalDuration
                    val ent = if(languageStateMap.containsKey(durationsum.id.languageName !!)){
                            languageStateMap.get(durationsum.id.languageName)
                        } else {
                            LanguageHoursEntity(
                                languageId = durationsum.id.languageId,
                                languageName = durationsum.id.languageName
                            )
                        }
                    ent?.totalCount = totalCount.toInt()
                    ent?.totalDuration = ent?.totalDuration?.plus(totalPendingDuration ?: 0.0f)
                    ent?.pendingCount = totalPending
                    ent?.pendingDuration = totalPendingDuration?.toDouble()
                    languageStateMap.put(durationsum.id.languageName !!,ent!! )

                }
                MediaAcceptanceState.ACCEPTED -> {
                    totalAccepted = durationsum.recordingCount
                    totalAcceptedDuration = durationsum.totalDuration
                    val ent = if(languageStateMap.containsKey(durationsum.id.languageName !!)){
                        languageStateMap.get(durationsum.id.languageName)
                    } else {
                        LanguageHoursEntity(
                            languageId = durationsum.id.languageId,
                            languageName = durationsum.id.languageName
                        )
                    }
                    ent?.totalCount = totalCount.toInt()
                    ent?.totalDuration = ent?.totalDuration?.plus(totalAcceptedDuration ?: 0.0f)
                    ent?.acceptedCount = totalAccepted
                    ent?.acceptedDuration = totalAcceptedDuration?.toDouble()
                    languageStateMap.put(durationsum.id.languageName !!,ent!! )
                }
                MediaAcceptanceState.REJECTED -> {
                    totalRejected = durationsum.recordingCount
                    totalRejectedDuration = durationsum.totalDuration

                    val ent = if(languageStateMap.containsKey(durationsum.id.languageName !!)){
                        languageStateMap.get(durationsum.id.languageName)
                    } else {
                        LanguageHoursEntity(
                            languageId = durationsum.id.languageId,
                            languageName = durationsum.id.languageName
                        )
                    }
                    ent?.totalCount = totalCount.toInt()
                    ent?.totalDuration = ent?.totalDuration?.plus(totalRejectedDuration ?: 0.0f)
                    ent?.rejectedCount = totalRejected
                    ent?.rejectedDuration = totalRejectedDuration?.toDouble()
                    languageStateMap.put(durationsum.id.languageName !!,ent!! )
                }
                null -> TODO()
            }

        }
        logger.info("deleting all language items")
        languageHoursImpl.deleteAllItems()
        logger.info("saving  all language items")
        languageHoursImpl.saveAll(languageStateMap.values.toList())
        logger.info("added to database: ${languageStateMap.values.toList()}")
//        languageHoursImpl.deleteAllItems()
//
    }
}