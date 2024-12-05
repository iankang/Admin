package com.thinkauth.thinkfusionauth.utils.async

import com.thinkauth.thinkfusionauth.entities.TotalHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.models.responses.DurationSum
import com.thinkauth.thinkfusionauth.repository.TotalHourRepository
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MediaEntityAggregationUtil(
    private val mediaEntityService: MediaEntityService,
    private val totalHourRepository: TotalHourRepository,
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Async
    fun setTotalHourEntity(){
        try {
            var totalEverything = mediaEntityService.aggregateMediaEntities()
            logger.info("media entity metrics: ${totalEverything}")
            var totalAccepted: Int? = 0
            var totalAcceptedDuration: Float? = 0.0f
            var totalRejected: Int? = 0
            var totalRejectedDuration: Float? = 0.0f
            var totalPending: Int? = 0
            var totalPendingDuration: Float? = 0.0f

            totalEverything.forEach { durationSum: DurationSum ->
                when (durationSum.id) {
                    MediaAcceptanceState.PENDING -> {
                        totalPending = durationSum.stateCount
                        totalPendingDuration = durationSum.totalDuration

                    }

                    MediaAcceptanceState.ACCEPTED -> {
                        totalAccepted = durationSum.stateCount
                        totalAcceptedDuration = durationSum.totalDuration

                    }

                    MediaAcceptanceState.REJECTED -> {
                        totalRejected = durationSum.stateCount
                        totalRejectedDuration = durationSum.totalDuration

                    }

                    null -> {


                    }
                }
            }

            val ent = TotalHoursEntity(
                totalDuration = totalAcceptedDuration?.plus(totalPendingDuration ?: 0.0f)
                    ?.plus(totalRejectedDuration ?: 0.0f)?.toDouble(),
                totalCount = totalAccepted?.plus(totalRejected ?: 0)?.plus(totalPending ?: 0),
                acceptedCount = totalAccepted,
                acceptedDuration = totalAcceptedDuration?.toDouble(),
                rejectedCount = totalRejected,
                rejectedDuration = totalRejectedDuration?.toDouble(),
                pendingCount = totalPending,
                pendingDuration = totalPendingDuration?.toDouble()
            )
            logger.info("totals: ${ent}")
            totalHourRepository.deleteAll()
            totalHourRepository.save(ent)
        }catch (e:Exception){
            logger.error("e: ${e.stackTrace}")
        }
    }
}