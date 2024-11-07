package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import com.thinkauth.thinkfusionauth.entities.SentenceUploadEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.repository.SentenceEntityRepository
import com.thinkauth.thinkfusionauth.repository.SentenceUploadRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper
import io.swagger.v3.oas.integration.StringOpenApiConfigurationLoader.LOGGER
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SentenceUploadService(
    private val sentenceUploadRepository: SentenceUploadRepository,
    private val sentenceManagement: SentenceEntityRepository,
    private val fileProcessingHelper: FileProcessingHelper,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userManagementService: UserManagementService,
    private val mediaEntityService: MediaEntityService,
    private val fileManagerService: StorageService,
    @Value("\${minio.bucket}") private val bucketName: String

) {

    fun addSentenceUpload(sentenceId:String, file: MultipartFile): SentenceUploadEntity {
        val sentence = sentenceManagement.findById(sentenceId).get();
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION,file.originalFilename!!)
//        sentence.audio = path.toString()
        val sentenceUpload = SentenceUploadEntity(
            SentenceEntitie = sentence,
            mediaPathId = path
        )
        val finalCollection = sentenceUploadRepository.save(sentenceUpload)
        val user = userManagementService.fetchLoggedInUserEntity()

        val response = fileManagerService.uploadFile(bucketName, path, file.inputStream)
        LOGGER.info("uploading response: ${response.toString()}")
        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(
            file,
            path,
            BucketName.VOICE_COLLECTION,
            sentence.id,
            sentence.business?.id,
            user
        )
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)

        return finalCollection
    }

    fun addAudioEvent(
        file: MultipartFile, SentenceEntitie: SentenceEntitie
    ): SentenceUploadEntity {
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION, file.name)
        LOGGER.info("file_path_add_audio: "+ path.toString())
//        SentenceEntitie.audio = path
        val sentenceUpload = SentenceUploadEntity(
            SentenceEntitie = SentenceEntitie,
            mediaPathId = path
        )
        val finalCollection = sentenceUploadRepository.save(sentenceUpload)
        val user = userManagementService.fetchLoggedInUserEntity()
        LOGGER.info("userInfo add audio event: ${user}")

        val response = fileManagerService.uploadFile(bucketName, path, file.inputStream)
        LOGGER.info("uploading response: ${response.toString()}")
        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(
            file,
            path,
            BucketName.VOICE_COLLECTION,
            SentenceEntitie.id,
            SentenceEntitie.business?.id,
            user
            )
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)
        LOGGER.info("finalCollection: "+ finalCollection.toString())
        return finalCollection
    }
}