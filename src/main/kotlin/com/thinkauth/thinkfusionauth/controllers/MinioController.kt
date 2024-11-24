package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.StorageService
import com.thinkauth.thinkfusionauth.utils.BucketName
import io.minio.GetObjectResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.juli.logging.Log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.InputStreamResource
import org.springframework.data.mongodb.repository.Query
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/minio")
@Tag(name = "Minio", description = "This manages media in the system.")

 class MinioController(
    private val applicationEventPublisher: ApplicationEventPublisher,
     private val storageService: StorageService,
    @Value("\${minio.bucket}")
     private val thinking:String
 ){

    private val LOGGER: Logger = LoggerFactory.getLogger(MinioController::class.java)

    @PostMapping(value=["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "upload a file", description = "Uploads a file", tags = ["Minio"])
     fun uploadMedia(
         @RequestPart("file") file: MultipartFile
     ){
         val path = thinking+ File.separator+BucketName.USER_ACCOUNT_PROFILE.name+File.separator+file.originalFilename
//         val onMediaUploadItemEvent = OnMediaUploadItemEvent(
//             file,
//             path,
//             BucketName.VOICE_COLLECTION,
//             null,
//             null
//         )
//         applicationEventPublisher.publishEvent(onMediaUploadItemEvent)
        try {
            storageService.uploadFile(thinking,BucketName.USER_ACCOUNT_PROFILE.name+File.separator+file.originalFilename, file.inputStream)
        }catch (e:Exception){
            LOGGER.error("error: ${e.message}")
        }
     }

    @GetMapping("/images/{objectName}")
    @Operation(summary = "download a file", description = "Downloads a file", tags = ["Minio"])
    @ResponseBody
    fun getMedia(
        @PathVariable("objectName") objectname:String
    ): ResponseEntity<InputStreamResource>? {
        try {
            val imageStream = storageService.getObject(thinking,BucketName.USER_ACCOUNT_PROFILE.name+File.separator+objectname)
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectname + "\"")
                .body(imageStream);
        }catch (e:Exception){
            LOGGER.error("error: ${e.message}")
        }
        return null
    }

    @GetMapping("/botProfilePicture/{objectName}")
    @Operation(summary = "get bot profile picture", description = "gets a bot profile image", tags = ["Minio"])
    @ResponseBody
    fun getBotProfilePicture(
        @PathVariable("objectName") objectname:String
    ): ResponseEntity<InputStreamResource>? {
        try {
            val objectame = thinking+File.separator+BucketName.BOT_PROFILE_PIC.name+File.separator+objectname
            LOGGER.info("object: $objectame")
            val imageStream = storageService.getObject(thinking,objectame)
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + objectname + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(InputStreamResource(imageStream?.inputStream!!));
        }catch (e:Exception){
            LOGGER.error("error: ${e.message}")
        }
        return null
    }
    @GetMapping("/businessProfilePicture/{objectName}")
    @Operation(summary = "get a business profile picture", description = "gets a business profile picture", tags = ["Minio"])
    @ResponseBody
    fun getBusinessProfilePicture(
        @PathVariable("objectName") objectname:String
    ): ResponseEntity<InputStreamResource>? {
        try {
            val objectame = thinking+File.separator+BucketName.BUSINESS_PROFILE_PIC.name+File.separator+objectname
            LOGGER.info("object: $objectame")
            val imageStream = storageService.getObject(thinking,objectame)
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + objectname + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(InputStreamResource(imageStream?.inputStream!!));
        }catch (e:Exception){
            LOGGER.error("error: ${e.message}")
        }
        return null
    }
    @GetMapping("/imageUrl")
    @Operation(summary = "download a file", description = "Downloads an image", tags = ["Minio"])
    @ResponseBody
    fun geMediaStream(
        @RequestParam("url") url:String
    ): ResponseEntity<InputStreamResource>? {
        try {
            LOGGER.error("url: ", "$url")
            val imageStream = storageService.getObject(thinking,url)
            val filename = url.split("/").last()
            LOGGER.error("filename: ", "$filename")
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + filename + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageStream);
        }catch (e:Exception){
            LOGGER.error("error: ${e.message}")
        }
        return null
    }

    @GetMapping("/preSignedUrl")
    @Operation(summary = "presigned Url", description = "Set a presigned url", tags = ["Minio"])
    @ResponseBody
    fun getPresignedObjectUrl(
        @RequestParam("bucket") bucket:String,
        @RequestParam("objectName") objectname:String,
    ): ResponseEntity<String> {
        return try {
             ResponseEntity.ok(storageService.getPresignedUrl(bucket,objectname))
        }catch (e:Exception){
            LOGGER.error("getPresignedObjectUrl: ${e.message}")
            ResponseEntity.ok(e.message)
        }
    }
 }
