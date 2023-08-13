package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.services.StorageService
import com.thinkauth.thinkfusionauth.utils.BucketName
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Paths

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
    @PostMapping(value=["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "upload a file", description = "Uploads a file", tags = ["Minio"])
     fun uploadMedia(
         @RequestPart("file") file: MultipartFile
     ){
         val path = Paths.get(thinking+ File.separator+BucketName.USER_ACCOUNT_PROFILE.name+File.separator+file.originalFilename)
         val onMediaUploadItemEvent = OnMediaUploadItemEvent(file,path)
         applicationEventPublisher.publishEvent(onMediaUploadItemEvent)
     }
 }
