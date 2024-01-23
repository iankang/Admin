package com.thinkauth.thinkfusionauth.events.listener

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import com.thinkauth.thinkfusionauth.services.StorageService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.data.rest.core.mapping.ResourceType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

@Component
class OnMediaUploadItemListener(
    private val fileManagerService: StorageService,
    private val userManagementService: UserManagementService,
    private val mediaEntityService: MediaEntityService,
    @Value("\${minio.bucket}")
    private val bucketName:String
): ApplicationListener<OnMediaUploadItemEvent> {

    private val logger: Logger = LoggerFactory.getLogger(OnMediaUploadItemListener::class.java)

    @Async
    override fun onApplicationEvent(event: OnMediaUploadItemEvent) {

        logger.info("uploading media")

        uploadMedia(event)
        logger.info("Finished uploading media")
    }

    private fun uploadMedia(event: OnMediaUploadItemEvent){
        val multipartFile = event.file
        val path = event.copyLocation
        val resource = event.resource

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
        val response = fileManagerService.uploadFile(bucketName,path,multipartFile.inputStream)
        val user = userManagementService.fetchLoggedInUserEntity()
       val mediaEntity = MediaEntity(
           mediaName = resource.name,
           owner = user,
           mediaObject = response?.`object`()!!,
           mediaPathId = path
           )
        mediaEntityService.saveMediaEntity(mediaEntity)
    }
}