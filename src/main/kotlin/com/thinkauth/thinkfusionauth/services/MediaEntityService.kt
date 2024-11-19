package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.models.responses.UserLanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.jaudiotagger.audio.AudioFileIO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension
import kotlin.io.path.name


@Service
class MediaEntityService(
    private val mediaEntityRepository: MediaEntityRepository,
    private val userManagementService: UserManagementService,
    private val audioCollectionService: AudioCollectionService,
    private val languageService: LanguageService,
    private val audioManagementService: AudioCollectionService,
    private val userIgnoreService: SentenceUserIgnoreService,
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService,
    private val storageService: StorageService,
    @Value("\${minio.bucket} ")
    private val thinkResources: String,

) {
    private val logger: Logger = LoggerFactory.getLogger(MediaEntityService::class.java)
    @TrackExecutionTime
    fun saveMediaEntity(mediaEntity: MediaEntity): MediaEntity {
        return mediaEntityRepository.save(mediaEntity)
    }

    @TrackExecutionTime
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
    @TrackExecutionTime
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
    @TrackExecutionTime
    fun fetchMediaEntityById(id:String): MediaEntity {
        return mediaEntityRepository.findById(id).get()
    }

    @TrackExecutionTime
    fun fetchAllMediaEntityByUser(email:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByOwnerEmail(email)
    }

    @TrackExecutionTime
    fun fetchAllMediaEntityByBusinessId(businessId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllByBusinessId(businessId)
    }

    @TrackExecutionTime
    fun fetchAllMediaEntityBySentenceId(sentenceId:String): List<MediaEntity> {
        return mediaEntityRepository.findAllBySentenceId(sentenceId)
    }

    @TrackExecutionTime
    fun acceptMediaEntity(mediaId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(mediaId)
        mediaEntity.mediaState = MediaAcceptanceState.ACCEPTED
        return saveMediaEntity(mediaEntity)
    }
    @TrackExecutionTime
    fun rejectMediaEntity(mediaId: String): MediaEntity {
        val mediaEntity = fetchMediaEntityById(mediaId)
        mediaEntity.mediaState = MediaAcceptanceState.REJECTED
        //recycle sentence when the audio is rejected.
       if(mediaEntity.sentenceId != null){
            audioCollectionService.setSentenceNeedsUpload(mediaEntity.sentenceId ?: "",true)
       }
        return saveMediaEntity(mediaEntity)
    }
    @TrackExecutionTime
    fun countAllVoiceCollectionsByLanguageId(languageId: String): Long {
        return mediaEntityRepository.countAllByLanguageId(languageId)
    }
    @TrackExecutionTime
    fun countAllVoiceCollections(
    ):MutableMap<String,Long>{
        val voiceMap = mutableMapOf<String,Long>()
        val countMediaName =  mediaEntityRepository.countAllByMediaName("VOICE_COLLECTION")
        voiceMap["VoiceCollection"] = countMediaName
        return voiceMap
    }
    @TrackExecutionTime
    fun countAllVoiceCollectionsByAcceptanceState(
    ): MutableMap<MediaAcceptanceState, Long> {
        var voiceStates = mutableMapOf<MediaAcceptanceState,Long>()
        val states = MediaAcceptanceState.values()
        states.forEach { mediaAcceptanceState ->
            voiceStates[mediaAcceptanceState] = mediaEntityRepository.countAllByMediaStateAndMediaName(mediaAcceptanceState,"VOICE_COLLECTION")
        }
        return voiceStates
    }

    @TrackExecutionTime
    fun countVoiceCollectionsByAcceptanceStateAndLanguageId(
        languageId: String
    ): MutableMap<MediaAcceptanceState, Long> {
        var voiceStates = mutableMapOf<MediaAcceptanceState,Long>()
        val states = MediaAcceptanceState.values()
        states.forEach { mediaAcceptanceState ->
            voiceStates[mediaAcceptanceState] = mediaEntityRepository.countByMediaStateAndLanguageId(mediaAcceptanceState,languageId)
        }
        return voiceStates
    }

    @TrackExecutionTime
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
    @TrackExecutionTime
    fun countAllVoiceCollectionsByLoggedInUser(): MutableMap<String, Long> {
        val user = userManagementService.fetchLoggedInUserEntity()
        val recordCount = mediaEntityRepository.countAllByUsernameAndMediaName(user.username?:"","VOICE_COLLECTION")
        val userVoiceRecordingsMap = mutableMapOf<String,Long>()
        userVoiceRecordingsMap["voiceRecordingCount"] =recordCount
        return userVoiceRecordingsMap
    }
    @TrackExecutionTime
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
    @TrackExecutionTime
    fun findMediaEntitiesByStatus(
        mediaAcceptanceState: MediaAcceptanceState,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.desc("lastModifiedDate")))
        val mediaEntities = mediaEntityRepository.findAllByMediaState(mediaAcceptanceState, paging)
        return PagedResponse(
            mediaEntities.content,
            mediaEntities.number,
            mediaEntities.totalElements,
            mediaEntities.totalPages
        )
    }
    @TrackExecutionTime
    fun findMediaEntitiesByMediaAcceptanceStateAndLanguageId(
        mediaAcceptanceState: MediaAcceptanceState,
        languageId: String,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<MediaEntity>> {
        val paging = PageRequest.of(page,size, Sort.by(Sort.Order.asc("lastModifiedDate")))
        val mediaEntities = mediaEntityRepository.findAllByMediaStateAndLanguageId(
            mediaAcceptanceState, languageId, paging
        )
        return PagedResponse(
            mediaEntities.content,
            mediaEntities.number,
            mediaEntities.totalElements,
            mediaEntities.totalPages
        )
    }
    @TrackExecutionTime
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
    @TrackExecutionTime
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
    @TrackExecutionTime
    fun uploadMedia(event: OnMediaUploadItemEvent) {
        try {

            val path = event.copyLocation
            val resource = event.resource
            val sentenceId = event.sentenceId
            val businessId = event.businessId
            val user = event.user

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
                translatedText = sentence.englishTranslation,
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

    @TrackExecutionTime
    fun mediaEntityForSentenceExists(sentenceId: String): Boolean {
        return mediaEntityRepository.existsBySentenceId(sentenceId)
    }

    @TrackExecutionTime
    fun mediaEntityGetDuration(objectName:String){
        try {
            val filePath: Path = Paths
                .get(
                    thinkResources + File.separator+ BucketName.VOICE_COLLECTION.name + File.separator + StringUtils.cleanPath(
                       objectName
                    )
                )

            val inputStream = storageService.getObjectInputStream("thinking", objectName = BucketName.VOICE_COLLECTION.name+File.separator+filePath.fileName.name)
            // Temporarily save the audio file locally
            val tempFile: Path = Files.createTempFile("audio", ".mp3")
            if (inputStream != null) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING)
            }


            // Use jaudiotagger to read the audio duration
            val audioFile = AudioFileIO.readAs(tempFile.toFile(),"mp3")
            val audioHeader = audioFile.audioHeader
//        totalDurationInSeconds += audioHeader.trackLength

            logger.info("totalDuration: ${audioHeader.trackLength}")
            logger.info("totalAudioInfo: ${audioHeader.toString()}")
            // Clean up the temporary file
            Files.delete(tempFile)
        }catch (e:Exception){
            logger.error("error: ${e}")
        }
    }
}