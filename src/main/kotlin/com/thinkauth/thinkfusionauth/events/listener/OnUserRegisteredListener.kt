package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.events.OnUserRegisteredEvent
import com.thinkauth.thinkfusionauth.services.UserManagementService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class OnUserRegisteredListener(
    private val userManagementService: UserManagementService
): ApplicationListener<OnUserRegisteredEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnUserRegisteredEvent::class.java)
    @Async
    override fun onApplicationEvent(event: OnUserRegisteredEvent) {
        logger.info("starting to add user to user entity")
        addUserToUserEntity(event)
        logger.info("finishing adding user to user entity")
    }

    fun addUserToUserEntity(event: OnUserRegisteredEvent){
        val email = event.email
        userManagementService.addUserFromFusionAuthByEmail(email)
    }
}