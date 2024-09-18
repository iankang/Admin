package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class OnMediaUploadItemListener(
    private val fileManagerService: StorageService,
    private val userManagementService: UserManagementService,
    private val mediaEntityService: MediaEntityService,
    private val audioManagementService: AudioCollectionService,
    @Value("\${minio.bucket}") private val bucketName: String
) : ApplicationListener<OnMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnMediaUploadItemListener::class.java)

    @Async
    override fun onApplicationEvent(event: OnMediaUploadItemEvent) {

        logger.info("uploading media")

        uploadMedia(event)
        logger.info("Finished uploading media")
    }

    private fun uploadMedia(event: OnMediaUploadItemEvent) {
        try {
            val multipartFile = event.file
            val path = event.copyLocation
            val resource = event.resource
            val sentenceId = event.sentenceId
            val businessId = event.businessId
            val genderState = event.genderState

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
            logger.info("path: $path")
            val response = fileManagerService.uploadFile(bucketName, path, multipartFile.inputStream)
            val user = userManagementService.fetchLoggedInUserEntity()
            val sentence = audioManagementService.getAudioCollectionById(sentenceId!!)
            val mediaEntity = MediaEntity(
                mediaName = resource.name,
                owner = user,
                username = user.username ?: user.email,
                mediaObject = response?.`object`()!!,
                mediaPathId = path,
                sentenceId = sentenceId,
                actualSentence = sentence.sentence,
                languageId = sentence.language.id,
                languageName = sentence.language.languageName,
                businessId = businessId,
                genderState = genderState
            )
            mediaEntityService.saveMediaEntity(mediaEntity)
        }catch (e:Exception){
            logger.error("OnMediaUploadListener: ${e.toString()}")
        }
    }
}