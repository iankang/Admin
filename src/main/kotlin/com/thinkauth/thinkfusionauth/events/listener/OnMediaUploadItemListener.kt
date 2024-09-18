package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import kotlin.math.log

@Component
class OnMediaUploadItemListener(
    private val mediaEntityService: MediaEntityService,
) : ApplicationListener<OnMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnMediaUploadItemListener::class.java)


    override fun onApplicationEvent(event: OnMediaUploadItemEvent) {

        logger.info("uploading media")

        mediaEntityService.uploadMedia(event)
        logger.info("Finished uploading media")
    }

}