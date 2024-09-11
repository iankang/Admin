package com.thinkauth.thinkfusionauth.config

import com.github.javafaker.Faker
import com.thinkauth.thinkfusionauth.entities.*
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.requests.BusinessRequest
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileIndustryRequest
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.repository.SentenceUploadRepository
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.repository.impl.ConversationImpl
import com.thinkauth.thinkfusionauth.services.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DataLoader(
    private val languageService: LanguageService,
    private val scrapingService: ScrapingService,
    private val audioCollectionService: AudioCollectionService,
    private val minioService: MinioService,
    private val faker:Faker,
    private val businessService: BusinessService,
    private val industryService: CompanyProfileIndustryService,
    private val mediaEntityService: MediaEntityRepository,
    private val conversationService: ConversationImpl,
    private val botInfoImpl: BotInfoImpl,
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
                val randBiz = businessService.getBusinesses().random()
                val collection = AudioCollectionRequest(it.vernac, language.id!!, it.engTranslation, randBiz.id)
                logger.info("collection: " + collection.toString())
                audioCollectionService.addSentenceCollection(collection)
            }

        }
    }

    fun createBusinesses(){
        if(businessService.getBusinessCount() == 0L){
            repeat(10){
                val biz = BusinessRequest(
                    businessName = faker.company().name(),
                    businessDescription = faker.company().bs()
                )
                logger.info("business: "+ biz)
                businessService.addBusiness(biz)
            }

        }
    }

    fun industryItems(){
        if(industryService.fetchIndustryCount() == 0L){
            val industryList = mutableListOf("Agriculture and Agribusiness","Banking and Finance","Telecommunications",
                "Information Technology","Manufacturing and Industrial","Retail and Wholesale","Real Estate and Construction",
                "Healthcare and Pharmaceuticals","Energy and Utilities","Tourism and Hospitality","Transport and Logistics",
                "Education and Training","Media and Entertainment","Non-Profit and NGOs","Legal and Professional Services",
                "Mining and Extractive Industries","Government and Public Sector")

            industryList.forEach { industry ->
                industryService.addCompanyProfileIndustry(CompanyProfileIndustryRequest(industry))
            }
            logger.info("industryCount: "+ industryService.fetchIndustryCount())
        }
    }

    fun backdateAllMediaEntities(){
        val mediaEntities = mediaEntityService.findAllByMediaName("VOICE_COLLECTION")

        mediaEntities.forEach {

            val sentence = audioCollectionService.getAudioCollectionById(it.sentenceId!!)
            it.languageId = sentence.language.id
            it.username = it.owner.username ?: ""
            logger.info("modifying the media: "+ it)
            mediaEntityService.save(it)
        }

    }
    fun backdateAllConversations(){
        val mediaEntities = conversationService.findEverythingPaged(0,1000)

        mediaEntities.item.filter { conversation: Conversation -> conversation.conversationTitle == null }.forEachIndexed { index:Int,conversation: Conversation ->
            conversation.conversationTitle = "title $index"
            conversationService.createItem(conversation)
        }

    }

    fun addDescriptionsForBots(){
        botInfoImpl.findEverythingPaged(0,1000).item.filter { it.botDescription == null }.forEach {
            it.botDescription = faker.lorem().paragraph()
            botInfoImpl.createItem(it)
        }
    }

    fun backdateBotInfo(){
        botInfoImpl.findEverythingPaged(0,1000).item.forEach { botInformation: BotInformation ->
            botInformation.botType = BotTypeEnum.HISTORY
            botInformation.botDescription = "This is a ${botInformation.botName}. It belongs to Nick. This is a sample description"
            logger.info("bot info backdated: $botInformation")
            botInfoImpl.updateItem(botInformation.id!!,botInformation)
        }
    }

    override fun run(vararg args: String?) {
        logger.debug("starting to run the commandline runner")
        createBusinesses()
        loadingLanguageData()
        checkIfBucketIsAvailable()
        addSwahiliSentences()
        industryItems()
        backdateAllConversations()
        addDescriptionsForBots()
//        backdateAllMediaEntities()
    }
}