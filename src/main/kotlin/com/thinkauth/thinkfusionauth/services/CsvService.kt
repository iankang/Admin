package com.thinkauth.thinkfusionauth.services

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import com.thinkauth.thinkfusionauth.entities.enums.SentenceDocumentState
import com.thinkauth.thinkfusionauth.events.OnSentenceDocumentMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.events.OnUserRegisteredEvent
import com.thinkauth.thinkfusionauth.exceptions.BadRequestException
import com.thinkauth.thinkfusionauth.models.requests.DialectRequest
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.responses.LangAndDialect
import com.thinkauth.thinkfusionauth.models.responses.SentenceDocumentCSV
import com.thinkauth.thinkfusionauth.repository.impl.SentenceDocumentImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class CsvService(
    private val sentenceDocumentImpl: SentenceDocumentImpl,
    private val languageService: LanguageService,
    private val dialectService: DialectService,
    private val audioCollectionService: AudioCollectionService
) {

    private val logger: Logger = LoggerFactory.getLogger(CsvService::class.java)

    fun uploadCsvFile(file: MultipartFile): List<SentenceDocumentCSV> {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null

        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            val csvToBean = createCSVToBean(fileReader)

            return csvToBean.parse()
        } catch (ex: Exception) {
            throw Exception("Error during csv import")
        } finally {
            closeFileReader(fileReader)
        }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty) throw BadRequestException("Empty file")
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<SentenceDocumentCSV> =
        CsvToBeanBuilder<SentenceDocumentCSV>(fileReader)
            .withType(SentenceDocumentCSV::class.java)
            .withSkipLines(1)
            .withIgnoreLeadingWhiteSpace(true).build()

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw Exception("Error during csv import")
        }
    }

    @Async
    fun processCSV(event: OnSentenceDocumentMediaUploadItemEvent) {
        val multipartFile = event.file
        val business = event.business
        val fileId = event.fileId

        val csvItems = uploadCsvFile(multipartFile)
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
                csvItems.map { LangAndDialect(language = it.language, dialect = it.dialect) }.distinct()
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
            val sentences = mutableListOf<SentenceEntitie>()
            csvItems.map {
                if (!audioCollectionService.sentenceExistsBySentence(it.localLanguage ?: "")) {
                    logger.info("sentence doesn't exist: {}", it)
                    sentences.add(
                        SentenceEntitie(
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