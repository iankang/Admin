package com.thinkauth.thinkfusionauth.config.dataloading

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import com.thinkauth.thinkfusionauth.entities.*
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.requests.BusinessRequest
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileIndustryRequest
import com.thinkauth.thinkfusionauth.models.responses.*
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.repository.SentenceEntityRepository
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.repository.impl.ConstituencyImpl
import com.thinkauth.thinkfusionauth.repository.impl.ConversationImpl
import com.thinkauth.thinkfusionauth.repository.impl.CountyServiceImple
import com.thinkauth.thinkfusionauth.services.*
import com.thinkauth.thinkfusionauth.utils.async.MediaEntityLanguageMetricsAggregationUtil
import io.fusionauth.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class DataLoader(
    private val languageService: LanguageService,
    private val scrapingService: ScrapingService,
    private val audioCollectionService: AudioCollectionService,
    private val sentenceEntityRepository: SentenceEntityRepository,
    private val minioService: MinioService,
    private val faker:Faker,
    private val businessService: BusinessService,
    private val industryService: CompanyProfileIndustryService,
    private val mediaEntityService: MediaEntityRepository,
//    private val mediaEntityRealService: MediaEntityService,
    private val conversationService: ConversationImpl,
    private val botInfoImpl: BotInfoImpl,
    private val userManagementService: UserManagementService,
    private val countyService: CountyServiceImple,
    private val constituencyImpl: ConstituencyImpl,
    private val languageHoursService: LanguageHoursService,
    private val totalHourService: TotalHourService,
    private val uploadStateService: MediaEntityUserUploadStateService,
    private val approvalStateService: MediaEntityUserApprovalStateService,
    private val dialectService: DialectService,
    private val mongoTemplate: MongoTemplate,
    private val mongoAggregateKey: MediaEntityLanguageMetricsAggregationUtil,
    @Value("\${minio.bucket}") private val bucketName: String
) : CommandLineRunner {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    val mapper = ObjectMapper()

    val countyTypeReference = object : TypeReference<ArrayList<CountyEntity>>() {}
    val countyTypeInputStream: InputStream? = TypeReference::class.java.getResourceAsStream("/json/counties.json")

    val constituencyTypeReference = object : TypeReference<ArrayList<CountyResponse>>() {}
    val constituencyTypeInputStream: InputStream? = TypeReference::class.java.getResourceAsStream("/json/counties_constituencies.json")

    fun addCounties(){
        val countyJson = mapper.readValue(countyTypeInputStream, countyTypeReference)
        if (countyJson.size > countyService.count()) {
           val counties =  countyJson.map { county -> CountyEntity(county.countyId, county.name,county.code, county.capital) }
            countyService.saveAll(counties)
        }
    }

    fun addConstituencies(){
        val constituencyData = mapper.readValue(constituencyTypeInputStream,constituencyTypeReference)
        if(constituencyData.size > constituencyImpl.count() ){
            val constitutes:MutableList<ConstituencyEntity> = mutableListOf()
            constituencyData.forEach { countyResponse: CountyResponse ->
                countyResponse.constituencies?.forEach { constituency: Constituency? ->
                    constituency?.wards?.forEach {
                        val constituent = ConstituencyEntity(
                            wardName = it ?: "",
                            countyId = countyResponse.no ?: 0,
                            countyName = countyResponse?.name ?: "",
                            constituencyName = constituency.name
                        )
                        logger.info("adding: {}",constituent)

                        constitutes.add(constituent)
                    }
                }
            }

            constituencyImpl.saveAll(constitutes)
        }
    }
    fun loadingLanguageData() {
        if (languageService.getLanguagesCount() == 0L) {
            logger.debug("loading data from wikipedia....")
            val languages = languageService.downloadLanguagesFromWikipedia().filter { it.Country == "Kenya" }.toMutableList()
            logger.debug("finished loading data from wikipedia...")
            logger.debug("adding kenyan languages to table")
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
            it.languageId = sentence.language?.id
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

//    fun backdateAllUserInfo(){
//        logger.info("trying to run")
//        val userSet = mutableSetOf<UserEntity>()
//        val allUsers = userManagementService.fetchAllUsers().toSet()
//        userSet.addAll(allUsers)
//        userManagementService.deleteAllUsers()
//        userManagementService.addAllUsers(allUsers.toList())
//        logger.info("allusers: ")
//    }

    fun userInfoGreaterThanOne(){
        logger.info("users must be unique")
        val allUserEmails = userManagementService.fetchAllUsers().map { it.email }.toSet()
        allUserEmails.forEach {
            val userCount = userManagementService.countUserInstancesEmail(it ?: "")
            if(userCount>1) {
                logger.info("username: ${it}, count: ${userCount}")
                val allRelatedUsers = userManagementService.fetchAllUsersWithEmail(it ?: "").first()
                userManagementService.deleteAllUsersByEmail(it ?: "")
                userManagementService.addUserEntity(allRelatedUsers)
                logger.info("lastuser: ${allRelatedUsers}")
            }
        }
    }

    fun somaliUpdate(){
        val startPage = 0
        val increment = 1
        val somaliSentences = audioCollectionService.getAllSentencesByLanguageId("66eab0b9b47b7539e1262cfe",0 ,10)
        logger.info("somaliCount: {}",somaliSentences.item.size)
        val requiredSomaliLanguage = audioCollectionService.getLanguage("66eab0b9b47b7539e1262d00")
        val requiredSomaliDialect = dialectService.getDialectById("66ebaf2fca5b937d85f4b05f")
        logger.info("requiredSomaliLanguage: {}", requiredSomaliLanguage.toString())
        for (page in startPage..somaliSentences.totalPages step increment){
            logger.info("running for page: {}", page)
            val fetchedSomali = audioCollectionService.getAllSentencesByLanguageId("66eab0b9b47b7539e1262cfe",page ,10)
            val mappedValues = fetchedSomali.item.map {
                if (requiredSomaliLanguage != null) {
                    it.language = requiredSomaliLanguage
                    it.dialect = requiredSomaliDialect
                }
                it
            }
            audioCollectionService.saveAllSentences(mappedValues)
            logger.info("page {} of {}",page,fetchedSomali.totalPages)
        }
    }

    fun somaliUploadUpdate(){
        val startPage = 0
        val increment = 1
        val somaliEntities = mediaEntityService.findAllByLanguageId("66eab0b9b47b7539e1262cfe")
        logger.info("somaliEntities: ${somaliEntities.size}")
        val requiredSomaliLanguage = audioCollectionService.getLanguage("66eab0b9b47b7539e1262d00")
        logger.info("requiredSomali: ${requiredSomaliLanguage}")
        val somalisToAdd = somaliEntities.map {
            it.languageId = "66eab0b9b47b7539e1262d00"
            it
        }
        logger.info("somalis: ${somalisToAdd}")
       mediaEntityService.saveAll(somalisToAdd)
    }

    fun removeSwahili(){
        logger.info("removing swahili")
        audioCollectionService.deleteAllSentencesByLanguageId("66eab0b9b47b7539e1262f36")
    }

//    fun mediaEntityStatusChanger(){
//       val mediaEntities = mediaEntityService.findAllByMediaState(MediaAcceptanceState.ACCEPTED)
//        mediaEntities.forEach {
//            if(audioCollectionService.audioCollectionExists(it.sentenceId ?: "")) {
//                val sent = audioCollectionService.getAudioCollectionById(it.sentenceId ?: "")
//                sent.needUploads = false
//                audioCollectionService.saveSentence(sent)
//                logger.info("sentence: {}", sent.toString())
//
//            }
//        }
//    }

    fun sentenceRemoval(){
        val sentences = audioCollectionService.getAllSentences(0, 200).item.filter { it.language == null }
        sentences.forEach {
            sentenceEntityRepository.deleteById(it.id!!)
            logger.info("deleting: {}", it.sentence)
        }

    }

    fun mediaEntityBackdateSentenceUploaded(){
        val audios = mediaEntityService.findAll(Sort.by(Sort.Order.desc("createdDate")))
        logger.info("total number of media entities: ${audios.size}")
        audios.forEach {
            if(audioCollectionService.audioCollectionExists(it.sentenceId ?: "")){
                audioCollectionService.setSentenceNeedsUpload(it.sentenceId ?: "",false)
            }
        }
    }

//    fun uploaderBiodata(){
//
//        userManagementService.fetchAllUsers().forEach {
////            logger.info("user: {}",it.email)
////            val user = userManagementService.addUserFromFusionAuthByEmail(it.email!!)
//            logger.info("result: {}", it)
//        }
//
//    }

    fun encoder(){
        val text = "Aing? n? matham?ririo njera-in? iria ir? nja ya Kashmir."

        // Encode the string to UTF-8 byte array
        val utf8Bytes = text.toByteArray(Charsets.UTF_8)

        // Convert the UTF-8 byte array back to a string (to verify encoding)
        val encodedText = String(utf8Bytes, Charsets.UTF_8)

      logger.info("encoded: {}",encodedText)
    }


//    fun kikuyu(){
//        val sents = audioCollectionService.getAudioCollectionByLanguageId("66ebc5d7b52e2b22ba67317e", 0 , 50)
//        val filtere = sents.filter { sentenceEntitie: SentenceEntitie ->
//            sentenceEntitie.createdDate>LocalDateTime.now().minusDays(1)
//        }
//        logger.info("yesterday: ${filtere}")
//    }

    fun uploadStatusChangeUsers(){
//        val users = userManagementService.fetchAllUsers()
//        users.forEach {
//            if(it.email != null){
//
////                userManagementService.addUserFromFusionAuthByEmail(it.email!!)
//                val user =userManagementService.fetchUserByEmail(it.email!!)
//                logger.info("nationalId: ${user?.data?.get("nationalId")}")
//            }
//        }
        val mediaEntityApprovals = approvalStateService.getAllApprovals().filter { it.nationalId == null || it.nationalId == "null" }
        logger.info("count: ${mediaEntityApprovals.size}")
        mediaEntityApprovals.forEach { mediaEntityUserApprovalState: MediaEntityUserApprovalState ->
//            logger.info("user: ${mediaEntityUserApprovalState.owner}")

            val currUser =userManagementService.fetchUserByEmail(mediaEntityUserApprovalState.approverEmail!!)

            val approval = approvalStateService.getApprovalById(mediaEntityUserApprovalState.id!!)

            approval.nationalId = currUser?.data?.getOrDefault("nationalId",null).toString()
            approval.phoneNumber = currUser?.mobilePhone ?: ""
            approvalStateService.updateUserApproval(approval)

            logger.info("approval: $approval")
        }

    }
//
//    fun distinctLanguageId(){
//        val answer =mediaEntityService.findDistinctLanguageIdByMediaName("VOICE_COLLECTION")
//        logger.info("distinctLanguageIds: ${answer.toString()}")
//    }

//    fun getMediaEntitiesWithoutDuration(): Long {
//        val count = mediaEntityService.countAllByDuration(null)
//        logger.info("no duration media: ${count}")
//        if(count > 0L){
//            val medias = mediaEntityService.findAllByDuration(null)
//
//             medias.map { mediaEntity: MediaEntity ->
//                val objectName = mediaEntity.mediaPathId.split("/").last()
//                logger.info("mediaName: ${objectName}")
//                val duration = mediaEntityRealService.mediaEntityGetDuration(objectName)
//                mediaEntity.duration = duration
//                 logger.info("saving : ${mediaEntity}")
//                mediaEntityService.save(mediaEntity)
//            }
//        }
//        return count
//    }

    fun mediaEntityUpdate(){
        val mediaEntities = userManagementService.fetchAllUsers()
        val userMap = mutableMapOf<String?, User?>()
        mediaEntities.filter { it.email != null }.forEach {
            logger.info("userEmail: ${it.email}")
            if(it.email != null || it.email != "") {
                userMap[it.email] = userManagementService.fetchUserByEmail(it.email ?: "")
            }
        }
        logger.info("users: ${userMap}")
    }

    fun getMediaEntityDialectCount(){
        val matchOperation = match(Criteria("needUploads").`is`(true))
        val groupOperation: GroupOperation =
            group("dialect","dialectName").count().`as`("dialectCount")
        val aggregation = newAggregation(matchOperation, groupOperation)
        val aggregationResults: AggregationResults<SentenceDialectCount> =
            mongoTemplate.aggregate(aggregation, "sentenceEntitie", SentenceDialectCount::class.java)
        val dialectList = mutableListOf<DataSentenceCount>()
        aggregationResults.mappedResults.forEach {
            logger.info("item: ${it}")

//            if(it.id != null) {
//                val dialect = dialectService.getDialectById(it.id!!)
//                if (dialect != null) {
//                    val dialectEntity = DataSentenceCount(
//                        dialecId = it.id ?: "id",
//                        dialectName = dialect.dialectName ?: "",
//                        dialectCount = it.dialectCount ?: 0
//                    )
//                    dialectList.add(dialectEntity)
//                }
//            }
        }
        logger.info("dialect: ${dialectList}")
        logger.info("dialectCount: ${aggregationResults.mappedResults}")
    }

    fun getSomaliMain(){
        logger.info("somali Main")
        val paging = PageRequest.of(0, 5, Sort.by("lastModifiedDate").descending())
        val somalis = sentenceEntityRepository.findAllByDialectId("67435b3585a7090005c99264", paging)
        logger.info("totalElements: ${somalis.totalElements}")

    }

    fun countSomaliByLanguageId(){
        //localSomali
        val somali1 = sentenceEntityRepository.countAllByDialectId("67435b3585a7090005c99264")
        //legitSomali
        val somali2 = sentenceEntityRepository.countAllByDialectId("66ebaf2fca5b937d85f4b05f")

        logger.info("localSom: ${somali1}")
        logger.info("legitSom: ${somali2}")
    }

    fun convertLocalSomaliToCorrectSomali(){
        val paging = PageRequest.of(0, 5, Sort.by("lastModifiedDate").descending())
        sentenceEntityRepository.findAllByDialectId("67435b3585a7090005c99264",paging).content.forEach {
            logger.info("olderSomali: ${it}")
        }
    }

    fun aggregateSentences(){
        val matchOperation = match(Criteria("needsUpload").`is`(true))
        val groupOperation: GroupOperation =
            group("language.languageName","dialect.dialectName")
                .count().`as`("dialectCount")

        val aggregation = newAggregation(matchOperation, groupOperation)
        val aggregationResults: AggregationResults<SentenceDialectCount> =
            mongoTemplate.aggregate(aggregation, "sentenceEntitie", SentenceDialectCount::class.java)
        logger.info("raw_results: ${aggregationResults.rawResults}")
        logger.info("mapped_results: ${aggregationResults.mappedResults}")
    }

    fun removeNonKenyanLanguages(){
        val allLanguages = languageService.getLanguages()
        allLanguages.forEach { language: Language ->
            if(language.languageName != "Kenya"){
                logger.info("deleting: ${language}")
                languageService.deleteByLanguageId(languageId = language.id ?: "")
            }
        }
    }


    override fun run(vararg args: String?) {
        logger.debug("starting to run the commandline runner")
        createBusinesses()
        loadingLanguageData()
        checkIfBucketIsAvailable()
//        totalHourService.setTotalHourEntity()
//        mediaEntityService.deleteAllByLanguageId("66eab0b9b47b7539e1262f36")
//        mediaEntityRealService.countAllByLanguages()
//        removeSwahili()
//        addSwahiliSentences()
        industryItems()
//        backdateAllConversations()
        addCounties()
        addConstituencies()
        addDescriptionsForBots()
//        mediaEntityStatusChanger()

//        logger.info("running after 5 seconds delay")
//        backdateAllUserInfo()
//        backdateAllMediaEntities()
//        userInfoGreaterThanOne()

//        somaliUpdate()
//        removeSwahili()
//        sentenceRemoval()
//        mediaEntityBackdateSentenceUploaded()
//        somaliUploadUpdate()
//            uploaderBiodata()
//        encoder()
//        kikuyu()
//        uploadStatusChangeUsers()
//        distinctLanguageId()
//        getMediaEntitiesWithoutDuration()
//        languageHoursService.setAllDurations()
//        mediaEntityUpdate()
//        getSomaliMain()
//        getMediaEntityDialectCount()
//        countSomaliByLanguageId()
//        convertLocalSomaliToCorrectSomali()
//        aggregateSentences()

    }
}