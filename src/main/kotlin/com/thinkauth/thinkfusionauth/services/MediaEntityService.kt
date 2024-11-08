package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.events.listener.OnMediaUploadItemListener
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.models.responses.UserLanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MediaEntityService(
    private val mediaEntityRepository: MediaEntityRepository,
    private val userManagementService: UserManagementService,
    private val audioCollectionService: AudioCollectionService,
    private val languageService: LanguageService,
    private val audioManagementService: AudioCollectionService,
    private val userIgnoreService: SentenceUserIgnoreService,
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService

) {
    private val logger: Logger = LoggerFactory.getLogger(MediaEntityService::class.java)
    fun saveMediaEntity(mediaEntity: MediaEntity): MediaEntity {
        return mediaEntityRepository.save(mediaEntity)
    }


    fun fetchAllMediaEntityPaged(
        page:Int = 0,
        size:Int= 10
    ): PagedResponse<List<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val content = mediaEntityRepository.findAll(paging)
        return PagedResponse<List<MediaEntity>>(
            content.content,
            content.number,
            content.totalElements,
            content.totalPages
        )
    }

    fun fetchAllMediaEntityPagedByLanguageId(
        languageId:String,
        page:Int = 0,
        size:Int= 10
    ): PagedResponse<List<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val content = mediaEntityRepository.findAllByLanguageId(languageId,paging)
        return PagedResponse<List<MediaEntity>>(
            content.content,
            content.number,
            content.totalElements,
            content.totalPages
        )
    }
    fun fetchMediaEntityById(id:String): MediaEntity {
        return mediaEntityRepository.findById(id).get()
    }

    fun fetchAllMediaEntityByUser(email:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByOwnerEmail(email)
    }

    fun fetchAllMediaEntityByBusinessId(businessId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByBusinessId(businessId)
    }

    fun fetchAllMediaEntityBySentenceId(sentenceId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllBySentenceId(sentenceId)
    }

    fun acceptMediaEntity(mediaId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(mediaId)
        mediaEntity.mediaState = MediaAcceptanceState.ACCEPTED
        return saveMediaEntity(mediaEntity)
    }
    fun rejectMediaEntity(mediaId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(mediaId)
        mediaEntity.mediaState = MediaAcceptanceState.REJECTED
        return saveMediaEntity(mediaEntity)
    }

//    fun fetchMediaEntitiesByAcceptedState(
//        acceptedState:Boolean = false
//    ): List<MediaEntity> {
//        return mediaEntityRepository.findAllByAccepted(acceptedState)
//    }

    fun countAllVoiceCollectionsByLanguageId(languageId: String): Long {
        return mediaEntityRepository.countAllByLanguageId(languageId)
    }
    fun countAllVoiceCollections(
    ):MutableMap<String,Long>{
        val voiceMap = mutableMapOf<String,Long>()
        val countMediaName =  mediaEntityRepository.countAllByMediaName("VOICE_COLLECTION")
        voiceMap["VoiceCollection"] = countMediaName
        return voiceMap
    }

    fun countAllVoiceCollectionsByAcceptanceState(
    ): MutableMap<MediaAcceptanceState, Long> {
        var voiceStates = mutableMapOf<MediaAcceptanceState,Long>()
        val states = MediaAcceptanceState.values()
        states.forEach { mediaAcceptanceState ->
            voiceStates[mediaAcceptanceState] = mediaEntityRepository.countAllByMediaStateAndMediaName(mediaAcceptanceState,"VOICE_COLLECTION")
        }
        return voiceStates
    }

    fun countAllByLanguages(): MutableList<LanguageRecordingsResponse> {
        val languageList = mutableListOf<LanguageRecordingsResponse>()
        val languagesIds = mediaEntityRepository.findAllByMediaName("VOICE_COLLECTION").map { it.languageId }.distinct()
        languagesIds.forEach { languageId:String? ->

            val sentenceCount = audioCollectionService.getCountOfAllAudioCollectionByLanguageId(languageId!!)
            val recordingsCount = mediaEntityRepository.countAllByLanguageIdAndMediaName(languageId,"VOICE_COLLECTION")
            val language = languageService.getLanguageByLanguageId(languageId)
            languageList.add(LanguageRecordingsResponse(
                languageName = language.languageName,
                languageId = language.id,
                sentenceCount = sentenceCount ?: 0L,
                recordingCount = recordingsCount
            ))
        }
        return languageList
    }

    fun countAllVoiceCollectionsByLoggedInUser(): MutableMap<String, Long> {
        val user = userManagementService.fetchLoggedInUserEntity()
        val recordCount = mediaEntityRepository.countAllByUsernameAndMediaName(user.username?:"","VOICE_COLLECTION")
        val userVoiceRecordingsMap = mutableMapOf<String,Long>()
        userVoiceRecordingsMap["voiceRecordingCount"] =recordCount
        return userVoiceRecordingsMap
    }
    fun countAllVoiceCollectionsByLoggedInUserAndLanguage(): MutableList<UserLanguageRecordingsResponse> {
        val userRecordingsLanguageList = mutableListOf<UserLanguageRecordingsResponse>()
        val user = userManagementService.fetchLoggedInUserEntity()
        val languagesIds = mediaEntityRepository.findAllByMediaName("VOICE_COLLECTION").map { it.languageId }.distinct()
        languagesIds.forEach { languageId:String? ->
        val language = languageService.getLanguageByLanguageId(languageId!!)
            val recordCount = mediaEntityRepository.countAllByUsernameAndMediaNameAndLanguageId(user.username?:"","VOICE_COLLECTION", languageId)
            userRecordingsLanguageList.add(UserLanguageRecordingsResponse(language.languageName,languageId, recordCount))
        }

        return userRecordingsLanguageList
    }

    fun findAllVoiceCollectionsByLoggedInUser(
        page:Int,
        size:Int
    ): PagedResponse<List<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val user = userManagementService.fetchLoggedInUserEntity()
        val content = mediaEntityRepository.findAllByUsernameAndMediaName(user.username ?: "","VOICE_COLLECTION",paging)
        return PagedResponse<List<MediaEntity>>(
            content.content,
            content.number,
            content.totalElements,
            content.totalPages
        )
    }
    fun findAllVoiceCollectionsByLoggedInUserLanguageId(
        languageId: String,
        page:Int,
        size:Int
    ): PagedResponse<List<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val user = userManagementService.fetchLoggedInUserEntity()
        val content = mediaEntityRepository.findAllByUsernameAndMediaNameAndLanguageId( user.username!!,"VOICE_COLLECTION",languageId,paging)
        return PagedResponse<List<MediaEntity>>(
            content.content,
            content.number,
            content.totalElements,
            content.totalPages
        )
    }
    @Async
    fun uploadMedia(event: OnMediaUploadItemEvent) {
        try {

            val path = event.copyLocation
            val resource = event.resource
            val sentenceId = event.sentenceId
            val businessId = event.businessId
            val user = event.user

//        if (authentication !is AnonymousAuthenticationToken) {
//            val userPrincipal = authentication.principal as String
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
//        }
//        var bucko:String = when(resource){
//            BucketName.BUSINESS_PROFILE_PIC ->{
//                "$bucketName/"+ BucketName.BUSINESS_PROFILE_PIC+"/"+path.name
//            }
//
//            BucketName.STT_UPLOAD ->{
//                "$bucketName/"+ BucketName.STT_UPLOAD+"/"+path.name
//            }
//
//            BucketName.VOICE_COLLECTION ->{
//                "$bucketName/"+ BucketName.VOICE_COLLECTION+"/"+path.name
//            }
//
//            BucketName.USER_ACCOUNT_PROFILE->{
//                "$bucketName/"+ BucketName.USER_ACCOUNT_PROFILE+"/"+path.name
//            }
//        }
//        var bucko = mediaFullPath(resource, multipartFile.name).absolutePathString()

            logger.info("logged in user: ${user}")
            logger.info("path: $path")

            val sentence = audioManagementService.getAudioCollectionById(sentenceId!!)
            logger.info("sentence: $sentence")
            val mediaEntity = MediaEntity(
                mediaName = resource.name,
                owner = user!!,
                username = user.username ?: user.email,
                mediaPathId = path,
                sentenceId = sentenceId,
                actualSentence = sentence.sentence,
                languageId = sentence.language?.id,
                languageName = sentence.language?.languageName,
                businessId = businessId,
                genderState = user.genderState
            )
           val mediaent = saveMediaEntity(mediaEntity)
            mediaEntityUserUploadStateService.addMediaEntityUploadState(mediaent)

            //once uploaded, the sentence should not be visible
            if(user.email != null) {
                userIgnoreService.addSentenceUserIgnore(userId = user.email!!, sentenceId)
            } else{
                userIgnoreService.addSentenceUserIgnore(userId = user.username!!,sentenceId)
            }

            audioCollectionService.setSentenceNeedsUpload(sentenceId, false)
        }catch (e:Exception){
            logger.error("OnMediaUploadListener: ${e.toString()}")
        }
    }
}