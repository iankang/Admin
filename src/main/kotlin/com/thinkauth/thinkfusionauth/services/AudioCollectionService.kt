package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.entities.SentenceEntity
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.AudioCollectionRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper
import io.minio.GetObjectResponse
import org.apache.commons.io.IOUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.rest.core.mapping.ResourceType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.net.URLConnection
import javax.servlet.http.HttpServletResponse
import kotlin.io.path.Path


@Service
class AudioCollectionService(
    private val audioRepository: AudioCollectionRepository,
    private val languageService: LanguageService,
    private val businessService: BusinessService,
    @Value("\${minio.bucket}")
    private val bucketName:String,
    private val storageService: StorageService,
    private val fileProcessingHelper: FileProcessingHelper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionService::class.java)


    fun addSentenceCollection(audioCollectionRequest: AudioCollectionRequest): SentenceEntity {
        val language = languageService.getLanguageByLanguageId(audioCollectionRequest.languageId)
        val biz = businessService.getSingleBusiness(businessId = audioCollectionRequest.businessId!!)
        val collection = SentenceEntity(
            sentence = audioCollectionRequest.sentence,
            language = language!!,
            englishTranslation = audioCollectionRequest.englishTranslation,
            business = biz
        )
        return audioRepository.save(collection)
    }

    fun getAllSentences(
        page: Int, size: Int
    ): PagedResponse<MutableList<SentenceEntity>> {
        var sentenceEntityList = mutableListOf<SentenceEntity>()
        val paging = PageRequest.of(page, size)
        val sentenceEntityPage: Page<SentenceEntity> = audioRepository.findAll(paging)
        sentenceEntityList = sentenceEntityPage.content
        return PagedResponse<MutableList<SentenceEntity>>(
            sentenceEntityList,
            sentenceEntityPage.number,
            sentenceEntityPage.totalElements,
            sentenceEntityPage.totalPages
        )
    }

    fun audioCollectionExists(audioCollectionId: String): Boolean {
        return audioRepository.existsById(audioCollectionId)
    }

    fun audioCollectionExistsBySentence(audioCollectionRequest: AudioCollectionRequest): Boolean {
        return audioRepository.existsBySentence(audioCollectionRequest.sentence)
    }

    fun getAudioCollectionById(audioCollectionId: String): SentenceEntity {
        return audioRepository.findById(audioCollectionId).get()
    }

//    fun getMinioObject(
//        objectName:String
//    ): GetObjectResponse? {
//        return storageService.stream(bucketName, objectName)
//    }
    fun getMinioObject(objectName:String, response: HttpServletResponse){
        val inputStream = storageService.stream(bucketName, objectName)

        // Set the content type and attachment header.

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=${objectName}")
        response.contentType = fileProcessingHelper.getExtensionFromResource(BucketName.VOICE_COLLECTION)

        // Copy the stream to the response's output stream.

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.outputStream)
        response.flushBuffer()
    }

    fun getAudioCollectionByLanguageId(languageId: String): List<SentenceEntity> {
        return audioRepository.findAllByLanguageId(languageId)
    }


    fun getLanguage(languageId: String): Language? {
        return languageService.getLanguageByLanguageId(languageId)
    }

    fun languageIdExists(languageId: String): Boolean {
        return languageService.existsByLanguageId(languageId)
    }

    fun deleteAllAudioCollection() {
        audioRepository.deleteAll()
    }

    fun getCountOfAllAudioCollectionByLanguageId(languageId: String): Long? {
        return audioRepository.countAudioCollectionsByLanguageId(languageId)
    }

    fun audioSentencesCount(): Long {
        return audioRepository.count()
    }

    fun deleteAllSentences() {
        return audioRepository.deleteAll()
    }

    fun getAllSentencesByBusinessId(businessId:String): List<SentenceEntity> {
        return audioRepository.findAllByBusinessId(businessId)
    }
}