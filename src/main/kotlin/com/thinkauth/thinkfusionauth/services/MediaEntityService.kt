package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.models.responses.UserLanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MediaEntityService(
    private val mediaEntityRepository: MediaEntityRepository,
    private val userManagementService: UserManagementService,
    private val audioCollectionService: AudioCollectionService,
    private val languageService: LanguageService
) {

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
    fun fetchMediaEntityById(id:String): MediaEntity {
        return mediaEntityRepository.findById(id).get()
    }

    fun fetchAllMediaEntityByUser(email:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByCreatedByUser(email)
    }

    fun fetchAllMediaEntityByBusinessId(businessId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByBusinessId(businessId)
    }

    fun fetchAllMediaEntityBySentenceId(sentenceId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllBySentenceId(sentenceId)
    }

    fun acceptMediaEntity(sentenceId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(sentenceId)
        mediaEntity.mediaState = MediaAcceptanceState.ACCEPTED
        return saveMediaEntity(mediaEntity)
    }
    fun rejectMediaEntity(sentenceId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(sentenceId)
        mediaEntity.mediaState = MediaAcceptanceState.REJECTED
        return saveMediaEntity(mediaEntity)
    }

    fun fetchMediaEntitiesByAcceptedState(
        acceptedState:Boolean = false
    ): List<MediaEntity> {
        return mediaEntityRepository.findAllByAccepted(acceptedState)
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
}