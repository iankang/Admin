package com.thinkauth.thinkfusionauth.utils.async

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.repository.MediaEntityRepository
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.MediaEntityUserUploadStateService
import com.thinkauth.thinkfusionauth.services.SentenceUserIgnoreService
import com.thinkauth.thinkfusionauth.services.StorageService
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioSystem
import kotlin.io.path.name

@Service
class UploadMediaservice(
    private val audioManagementService: AudioCollectionService,
    private val mediaEntityRepository: MediaEntityRepository,
    private val userIgnoreService: SentenceUserIgnoreService,
    private val audioCollectionService: AudioCollectionService,
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService,
    private val storageService: StorageService,
    @Value("\${minio.bucket} ")
    private val thinkResources: String,
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @TrackExecutionTime
    @Async
    fun uploadingMedia(event: OnMediaUploadItemEvent) {
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

            val objectName = mediaEntity.mediaPathId.split("/").last()
            logger.info("mediaName: ${objectName}")
            val duration = mediaEntityGetDuration(objectName)
            logger.info("duration: ${duration}")
            mediaEntity.duration = duration
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
    fun mediaEntityGetDuration(objectName:String): Float? {
        try {
            val filePath: Path = Paths
                .get(
                    thinkResources + File.separator+ BucketName.VOICE_COLLECTION.name + File.separator + StringUtils.cleanPath(
                        objectName
                    )
                )

            val inputStream = storageService.getObjectInputStream("thinking", objectName = BucketName.VOICE_COLLECTION.name+ File.separator+filePath.fileName.name)
            // Temporarily save the audio file locally
            val tempFile: Path = Files.createTempFile("audio", ".wav")
            if (inputStream != null) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING)
            }

            val fileFormat: AudioFileFormat = AudioSystem.getAudioFileFormat(tempFile.toFile())
            logger.info("file: ${fileFormat} ")

            val denom = fileFormat.format.frameSize.times(fileFormat.format.frameRate)
            val duration = fileFormat.byteLength.div(denom)
            logger.info("duration: $duration")
            Files.delete(tempFile)
            return duration
        }catch (e:Exception){
            logger.error("error: ${e}")
        }
        return null
    }

    @TrackExecutionTime
    fun saveMediaEntity(mediaEntity: MediaEntity): MediaEntity {
        return mediaEntityRepository.save(mediaEntity)
    }
}