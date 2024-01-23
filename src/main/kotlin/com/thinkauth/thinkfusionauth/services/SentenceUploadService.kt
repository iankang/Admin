package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.SentenceEntity
import com.thinkauth.thinkfusionauth.entities.SentenceUploadEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.repository.AudioCollectionRepository
import com.thinkauth.thinkfusionauth.repository.SentenceUploadRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper
import io.swagger.v3.oas.integration.StringOpenApiConfigurationLoader.LOGGER
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SentenceUploadService(
    private val sentenceUploadRepository: SentenceUploadRepository,
    private val sentenceManagement: AudioCollectionRepository,
    private val fileProcessingHelper: FileProcessingHelper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    fun addSentenceUpload(sentenceId:String, file: MultipartFile): SentenceUploadEntity {
        val sentence = sentenceManagement.findById(sentenceId).get();
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION,file.originalFilename!!)
//        sentence.audio = path.toString()
        val sentenceUpload = SentenceUploadEntity(
            sentenceEntity = sentence,
            mediaPathId = path
        )
        val finalCollection = sentenceUploadRepository.save(sentenceUpload)

        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(file, path, BucketName.VOICE_COLLECTION)
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)

        return finalCollection
    }

    fun addAudioEvent(
        file: MultipartFile, sentenceEntity: SentenceEntity
    ): SentenceUploadEntity {
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION, file.name)
        LOGGER.info("file_path_add_audio: "+ path.toString())
//        sentenceEntity.audio = path
        val sentenceUpload = SentenceUploadEntity(
            sentenceEntity = sentenceEntity,
            mediaPathId = path
        )
        val finalCollection = sentenceUploadRepository.save(sentenceUpload)
        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(file, path, BucketName.VOICE_COLLECTION)
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)
        LOGGER.info("finalCollection: "+ finalCollection.toString())
        return finalCollection
    }
}