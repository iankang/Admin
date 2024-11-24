package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.LanguageHourUserEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.repository.impl.LanguageHourUserImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TotalHourUserService(
    private val totalHourUserImpl: LanguageHourUserImpl,
    private val userManagementService: UserManagementService,
    private val mediaEntityService: MediaEntityService
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    fun setTotalHourUser(
        email: String
    ): LanguageHourUserEntity? {
//        val user = userManagementService.loggedInUser()

        logger.info("user: ${email}")
        val medias = mediaEntityService.fetchAllMediaEntityByUser(email!!)
        if(medias.isNotEmpty()) {
            val totalCount = medias.size
            val totalDuration = medias.sumOf { it.duration?.toDouble() ?: 0.0 }
            val totalAccepted = medias.count { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name }
            val totalAcceptedDuration = medias.filter { it.mediaState.name == MediaAcceptanceState.ACCEPTED.name }
                .sumOf { it.duration?.toDouble() ?: 0.0 }
            val totalRejected = medias.count { it.mediaState.name == MediaAcceptanceState.REJECTED.name }
            val totalRejectedDuration = medias.filter { it.mediaState.name == MediaAcceptanceState.REJECTED.name }
                .sumOf { it.duration?.toDouble() ?: 0.0 }
            val totalPending = medias.count { it.mediaState.name == MediaAcceptanceState.PENDING.name }
            val totalPendingDuration = medias.filter { it.mediaState.name == MediaAcceptanceState.PENDING.name }
                .sumOf { it.duration?.toDouble() ?: 0.0 }

            val ent = LanguageHourUserEntity(
                userEntity = userManagementService.fetchUserEntityByEmail(email = email),
                email = email,
                totalCount = totalCount,
                totalDuration = totalDuration,
                acceptedDuration = totalAcceptedDuration,
                acceptedCount = totalAccepted,
                rejectedCount = totalRejected,
                rejectedDuration = totalRejectedDuration,
                pendingDuration = totalPendingDuration,
                pendingCount = totalPending
            )

            totalHourUserImpl.updateLanguageHourUser(email, ent)
            return ent
        } else {
            val ent = LanguageHourUserEntity(
                userEntity = userManagementService.fetchUserEntityByEmail(email = email),
                email = email,
                totalCount = 0,
                totalDuration = 0.0,
                acceptedDuration = 0.0,
                acceptedCount = 0,
                rejectedCount = 0,
                rejectedDuration = 0.0,
                pendingDuration = 0.0,
                pendingCount = 0
            )

            totalHourUserImpl.updateLanguageHourUser(email, ent)
            return ent
        }
    }

    fun getAllTotalHourUsers(): MutableList<LanguageHourUserEntity> {
        return totalHourUserImpl.getAllHourUsers()
    }

    fun getTotalHourUserByEmail(email:String): LanguageHourUserEntity? {
        if(totalHourUserImpl.numberOfUserHoursLogged(email)> 0L) {
            logger.info("user already tracked")
            return totalHourUserImpl.findUserByEmail(email)
        }
        return setTotalHourUser(email)
    }
}