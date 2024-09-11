package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.entities.*
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
@EnableAsync
class OnSentenceDocumentUploadedListener(
    private val csvService: CsvService,
    private val sentenceDocumentImpl: SentenceDocumentImpl,
    private val languageService: LanguageService,
    private val dialectService: DialectService,
    private val audioCollectionService: AudioCollectionService
) : ApplicationListener<OnSentenceDocumentMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnSentenceDocumentUploadedListener::class.java)

    @Async
    override fun onApplicationEvent(event: OnSentenceDocumentMediaUploadItemEvent) {

        logger.info("uploading sentences")
        processCSV(event)
        logger.info("finished uploading  sentences")
    }

    fun processCSV(event: OnSentenceDocumentMediaUploadItemEvent) {
        val multipartFile = event.file
        val business = event.business
        val fileId = event.fileId

        val csvItems = csvService.uploadCsvFile(multipartFile)
        if (csvItems.isNotEmpty()) {
            logger.info("adding sentences to db")
            uploadTheSentences(csvItems, business, fileId)
        }
    }

    fun uploadTheSentences(
        csvItems: List<SentenceDocumentCSV>, business: Business, fileId: String
    ) {
        try {

            val sentenceDoc = sentenceDocumentImpl.getSentenceDocumentByFileId(fileId)
            logger.info("sentence upload entity: {}", sentenceDoc)
            val dialects =
                csvItems.map { LangAndDialect(language = it.language, dialect = it.dialect)}.distinct()
            logger.info("dialectsMap: {}", dialects)
            val languageEntityMap: MutableMap<String, Language> = mutableMapOf()
            val dialectEntityMap: MutableMap<String, Dialect> = mutableMapOf()
            dialects.forEach { item ->
                if (!languageService.existsByLanguageName(item.language!!)) {
                    logger.info("language does not exist: {}", item.language)
                    languageEntityMap[item.language!!] = languageService.addLanguage(languageRequest = LanguageRequest(item.language!!))

                } else {
                    logger.info("language exists: {}", item.language)
                    languageEntityMap[item.language!!] = (languageService.findLanguageByLanguageName(item.language!!).first())!!
                }
                if (!dialectService.existsByDialectName(item.dialect!!)) {
                    logger.info("dialect does not exist: {}", item.dialect!!)
                    val languageStuff = languageService.findLanguageByLanguageName(item.language!!).first()
                    dialectEntityMap[item.dialect!!] =
                        dialectService.addDialect(DialectRequest(dialectName = item.dialect, languageId = languageStuff?.id))

                } else {
                    logger.info("dialect exists: {}", item.language)
                    dialectEntityMap[item.dialect!!] = dialectService.getDialectByDialectName(item.dialect!!)?.first()!!
                }
                logger.info("languages: {}, dialects: {}", languageEntityMap, dialectEntityMap)
            }
//     d
            val sentences = mutableListOf<SentenceEntity>()
            csvItems.map {
                if (!audioCollectionService.sentenceExistsBySentence(it.localLanguage ?: "")) {
                    logger.info("sentence doesn't exist: {}", it)
                    sentences.add(
                        SentenceEntity(
                            sentence = it.localLanguage,
                            language = languageEntityMap[it.language]!!,
                            dialect = dialectEntityMap[it.dialect],
                            englishTranslation = it.textTranslation,
                            topic = it.topic,
                            source = it.source,
                            business = business
                        )
                    )
                }
            }

            if (sentences.isNotEmpty()) {
                logger.info("Adding {} sentences", sentences.size)
                audioCollectionService.bulkAddSentences(sentences)
                sentenceDoc.sentenceCount = sentences.size
                sentenceDoc.sentenceDocumentState = SentenceDocumentState.PROCESSED

            } else {
                logger.info("sentences is empty")
                sentenceDoc.sentenceDocumentState = SentenceDocumentState.PROCESSED
            }
            sentenceDocumentImpl.createItem(sentenceDoc)
        } catch (e: Exception) {
            logger.error("something went wrong: {}", e.toString())
            val sentenceDoc = sentenceDocumentImpl.getSentenceDocumentByFileId(fileId)
            sentenceDoc.sentenceDocumentState = SentenceDocumentState.FAILED
            sentenceDocumentImpl.createItem(sentenceDoc)
        }
    }

}