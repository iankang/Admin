package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.TotalHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.repository.TotalHourRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TotalHourService(
    private val totalHourRepository: TotalHourRepository,
    private val mediaEntityService: MediaEntityService
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    @Scheduled(cron =  "0 0/5 * * * *")
    @Async
    fun setTotalHourEntity(){
        val totalEverything= mediaEntityService.findAllMediaEntities()
        logger.info("total found: ${totalEverything.size}")
        val totalCount = totalEverything.size
        val totalDuration = totalEverything.sumOf { it.duration?.toDouble() ?: 0.0 }
        val totalAccepted = totalEverything.count { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name }
        val totalAcceptedDuration = totalEverything.filter { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }
        val totalRejected = totalEverything.count { it.mediaState.name == MediaAcceptanceState.REJECTED.name }
        val totalRejectedDuration = totalEverything.filter { it.mediaState.name == MediaAcceptanceState.REJECTED.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }
        val totalPending = totalEverything.count { it.mediaState.name == MediaAcceptanceState.PENDING.name }
        val totalPendingDuration = totalEverything.filter { it.mediaState.name == MediaAcceptanceState.PENDING.name  }.sumOf { it.duration?.toDouble() ?: 0.0 }

        val ent = TotalHoursEntity(
            totalDuration = totalDuration,
            totalCount = totalCount,
            acceptedCount = totalAccepted,
            acceptedDuration = totalAcceptedDuration,
            rejectedCount = totalRejected,
            rejectedDuration = totalRejectedDuration,
            pendingCount = totalPending,
            pendingDuration = totalPendingDuration
        )
        logger.info("totals: ${ent}")
        totalHourRepository.deleteAll()
        totalHourRepository.save(ent)
    }

    fun getTotalHourEntity(): MutableList<TotalHoursEntity> {
        return totalHourRepository.findAll()
    }
}