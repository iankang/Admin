package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.TotalHoursEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.models.responses.DurationSum
import com.thinkauth.thinkfusionauth.repository.TotalHourRepository
import com.thinkauth.thinkfusionauth.utils.async.MediaEntityAggregationUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TotalHourService(
    private val totalHourRepository: TotalHourRepository,
    private val mediaEntityAggregationUtil: MediaEntityAggregationUtil
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    @Scheduled(cron =  "0 0/5 * * * *")
    fun runScheduledMetrics(){
        mediaEntityAggregationUtil.setTotalHourEntity()
    }

    fun getTotalHourEntity(): MutableList<TotalHoursEntity> {
        return totalHourRepository.findAll()
    }
}