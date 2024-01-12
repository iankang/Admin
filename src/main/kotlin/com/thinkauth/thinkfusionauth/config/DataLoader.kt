package com.thinkauth.thinkfusionauth.config

import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.LanguageService
import com.thinkauth.thinkfusionauth.services.MinioService
import com.thinkauth.thinkfusionauth.services.ScrapingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val languageService: LanguageService,
    private val scrapingService: ScrapingService,
    private val audioCollectionService: AudioCollectionService,
    private val minioService: MinioService,
    @Value("\${minio.bucket}") private val bucketName: String
) : CommandLineRunner {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun loadingLanguageData() {
        if (languageService.getLanguagesCount() == 0L) {
            logger.debug("loading data from wikipedia....")
            val languages = languageService.downloadLanguagesFromWikipedia()
            logger.debug("finished loading data from wikipedia...")
            logger.debug("adding languages to table")
            languageService.addLanguages(languages)
            logger.debug("finished adding languages to table")
        }
    }

    fun checkIfBucketIsAvailable() {
        if (!minioService.bucketExists(bucketName)) {
            logger.info("bucket does not exist, creating one.")
            minioService.makeBucket(bucketName)
        } else {
            logger.info("bucket is available, ignoring")
        }
    }

    fun addSwahiliSentences() {
        val language =
            languageService.findLanguageByLanguageName("Swahili").filter { it?.country?.lowercase() == "kenya" }.first()
        val sentencesCount = audioCollectionService.getCountOfAllAudioCollectionByLanguageId(language?.id!!)
        logger.info("sentences count by language: " + sentencesCount)
        if (sentencesCount == 0L) {
            logger.info("looking for swahili: " + language.toString())
            val sentences = scrapingService.fetchSwahiliWords()
            logger.info("sentences count: " + sentences.size)
            sentences.forEach {
//                collection.add()
                val collection = AudioCollectionRequest(it.vernac, language.id!!, it.engTranslation)
                logger.info("collection: " + collection.toString())
                audioCollectionService.addSentenceCollection(collection)
            }

        }
    }

    override fun run(vararg args: String?) {
        logger.debug("starting to run the commandline runner")
        loadingLanguageData()
        checkIfBucketIsAvailable()
        addSwahiliSentences()
    }
}