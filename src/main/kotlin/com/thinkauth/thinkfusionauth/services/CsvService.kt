package com.thinkauth.thinkfusionauth.services

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import com.thinkauth.thinkfusionauth.entities.enums.SentenceDocumentState
import com.thinkauth.thinkfusionauth.events.OnSentenceDocumentMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.exceptions.BadRequestException
import com.thinkauth.thinkfusionauth.models.requests.DialectRequest
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.responses.LangAndDialect
import com.thinkauth.thinkfusionauth.models.responses.SentenceDocumentCSV
import com.thinkauth.thinkfusionauth.repository.impl.SentenceDocumentImpl
import com.thinkauth.thinkfusionauth.utils.toStandardCase
import com.thinkauth.thinkfusionauth.utils.toTitleCase
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
    private val audioCollectionService: AudioCollectionService,
    private val mediaEntityService: MediaEntityService
) {

    private val logger: Logger = LoggerFactory.getLogger(CsvService::class.java)

    fun uploadCsvFile(file: MultipartFile, fileId: String): List<SentenceDocumentCSV> {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null

        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream,"UTF-8"))
            val csvToBean = createCSVToBean(fileReader)

            return csvToBean.parse()
        } catch (ex: Exception) {
            val sentenceDoc = sentenceDocumentImpl.getSentenceDocumentByFileId(fileId)
            sentenceDoc.failureReason = ex.toString()
            sentenceDocumentImpl.createItem(sentenceDoc)
            throw Exception("CsvService Error during csv import: ${ex.toString()}")
        } finally {
            closeFileReader(fileReader)

        }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty) throw BadRequestException("Empty file")
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<SentenceDocumentCSV> =
        CsvToBeanBuilder<SentenceDocumentCSV>(fileReader)
            .withSkipLines(1)
            .withType(SentenceDocumentCSV::class.java)
            .withIgnoreEmptyLine(true)
            .withIgnoreLeadingWhiteSpace(true).build()

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw Exception("CsvService Error during csv import")
        }
    }

    @Async
    fun processCSV(event: OnSentenceDocumentMediaUploadItemEvent) {
        val multipartFile = event.file
        val business = event.business
        val fileId = event.fileId

        val csvItems = uploadCsvFile(multipartFile, fileId)
        if (csvItems.isNotEmpty()) {
            logger.info("CsvService adding sentences to db")
            uploadTheSentences(csvItems, business, fileId)
        } else {
            logger.info("csvService is empty")
        }
    }

    fun uploadTheSentences(
        csvItems: List<SentenceDocumentCSV>, business: Business, fileId: String
    ) {
        try {

            val sentenceDoc = sentenceDocumentImpl.getSentenceDocumentByFileId(fileId)
            logger.info("csvItemsSize: ${csvItems.size}")
            val dialects = csvItems.map {
                    LangAndDialect(language = it.language?.toTitleCase(), dialect = it.dialect?.toTitleCase())
                }.distinct()
            logger.info("CsvService dialectsMap: {}", dialects)
            val languageEntityMap: MutableMap<String, Language> = mutableMapOf()
            val dialectEntityMap: MutableMap<String, Dialect> = mutableMapOf()
            dialects.forEach { item ->
                if (!languageService.existsByLanguageName(item.language?.toTitleCase()!!)) {
                    logger.info("CsvService language does not exist: {}", item.language?.toTitleCase())
                    languageEntityMap[item.language?.toTitleCase() ?: ""] =
                        languageService.addLanguage(languageRequest = LanguageRequest(item.language?.toTitleCase()!!))

                } else {
                    logger.info("CsvService language exists: {}", item.language)
                    languageEntityMap[item.language?.toTitleCase()!!] =
                        (languageService.findLanguageByLanguageName(item.language?.toTitleCase()!!).first { it?.country == "Kenya" })!!
                }
                if (!dialectService.existsByDialectName(item.dialect?.toTitleCase()!!)) {
                    logger.info("CsvService dialect does not exist: {}", item.dialect?.toTitleCase()!!)
                    val languageStuff =
                        languageService.findLanguageByLanguageName(item.language?.toTitleCase()!!).first { it?.country == "Kenya" }
                    dialectEntityMap[item.dialect?.toTitleCase()!!] = dialectService.addDialect(
                        DialectRequest(
                            dialectName = item.dialect?.toTitleCase(),
                            languageId = languageStuff?.id
                        )
                    )

                } else {
                    logger.info("CsvService dialect exists: {}", item.language?.toTitleCase())
                    dialectEntityMap[item.dialect?.toTitleCase()!!] = dialectService.getDialectByDialectName(item.dialect?.toTitleCase()!!)?.first()!!
                }
                logger.info("CsvService languages: {}, dialects: {}", languageEntityMap, dialectEntityMap)
            }
//     d
            val sentences = mutableListOf<SentenceEntitie>()
            var index = 0
            csvItems.map {
                index += 1
                logger.info("index: $index")
                if (!audioCollectionService.sentenceExistsBySentence(it.localLanguage ?: "")) {
                    logger.info("CsvService sentence doesn't exist: {}", it)

                    val sent = SentenceEntitie(
                        sentence = it.localLanguage,
                        language = languageEntityMap[it.language?.toTitleCase()]!!,
                        dialect = dialectEntityMap[it.dialect?.toTitleCase()],
                        englishTranslation = it.textTranslation,
                        topic = it.topic,
                        source = it.source,
                        business = business
                    )
                    sent.fileSource = fileId
                    sentences.add(sent)
                }
            }

            if (sentences.isNotEmpty()) {
                logger.info("CsvService Adding {} sentences", sentences.size)
                audioCollectionService.bulkAddSentences(sentences)
                sentenceDoc.sentenceCount = sentences.size
                sentenceDoc.sentenceDocumentState = SentenceDocumentState.PROCESSED

            } else {
                logger.info("CsvService sentences is empty")
                sentenceDoc.sentenceDocumentState = SentenceDocumentState.PROCESSED
            }
            sentenceDocumentImpl.createItem(sentenceDoc)
        } catch (e: Exception) {
            logger.error("CsvService something went wrong: {}", e.toString())
            val sentenceDoc = sentenceDocumentImpl.getSentenceDocumentByFileId(fileId)
            sentenceDoc.sentenceDocumentState = SentenceDocumentState.FAILED
            sentenceDoc.failureReason = e.message
            sentenceDocumentImpl.createItem(sentenceDoc)
        }
    }


}