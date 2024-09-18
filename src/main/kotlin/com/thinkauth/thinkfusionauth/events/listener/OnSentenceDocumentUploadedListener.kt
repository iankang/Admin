package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.entities.*
import com.thinkauth.thinkfusionauth.entities.enums.SentenceDocumentState
import com.thinkauth.thinkfusionauth.events.OnSentenceDocumentMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.models.requests.DialectRequest
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.responses.LangAndDialect
import com.thinkauth.thinkfusionauth.models.responses.SentenceDocumentCSV
import com.thinkauth.thinkfusionauth.repository.impl.SentenceDocumentImpl
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.CsvService
import com.thinkauth.thinkfusionauth.services.DialectService
import com.thinkauth.thinkfusionauth.services.LanguageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component

@Component
class OnSentenceDocumentUploadedListener(
    private val csvService: CsvService,

) : ApplicationListener<OnSentenceDocumentMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnSentenceDocumentUploadedListener::class.java)


    override fun onApplicationEvent(event: OnSentenceDocumentMediaUploadItemEvent) {

        logger.info("uploading sentences")
        csvService.processCSV(event)
        logger.info("finished uploading  sentences")
    }

}