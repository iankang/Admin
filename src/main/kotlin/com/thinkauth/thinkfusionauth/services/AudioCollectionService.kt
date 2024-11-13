package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.SentenceEntityRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper
import org.apache.commons.io.IOUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletResponse


@Service
class AudioCollectionService(
    private val sentenceRepository: SentenceEntityRepository,
    private val languageService: LanguageService,
    private val businessService: BusinessService,
    @Value("\${minio.bucket}")
    private val bucketName:String,
    private val storageService: StorageService,
    private val fileProcessingHelper: FileProcessingHelper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionService::class.java)

    @TrackExecutionTime
    fun addSentenceCollection(audioCollectionRequest: AudioCollectionRequest): SentenceEntitie {
        val language = languageService.getLanguageByLanguageId(audioCollectionRequest.languageId)
        val biz = businessService.getSingleBusiness(businessId = audioCollectionRequest.businessId!!)
        val collection = SentenceEntitie(
            sentence = audioCollectionRequest.sentence,
            language = language!!,
            englishTranslation = audioCollectionRequest.englishTranslation,
            business = biz
        )
        return sentenceRepository.save(collection)
    }

    @TrackExecutionTime
    fun bulkAddSentences(
        sentences:List<SentenceEntitie>
    ): MutableList<SentenceEntitie> {
        return sentenceRepository.saveAll(sentences)
    }

    @TrackExecutionTime
    fun getAllSentences(
        page: Int, size: Int
    ): PagedResponse<MutableList<SentenceEntitie>> {
        var SentenceEntitieList = mutableListOf<SentenceEntitie>()
        val paging = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending())
        val SentenceEntitiePage: Page<SentenceEntitie> = sentenceRepository.findAll(paging)
        SentenceEntitieList = SentenceEntitiePage.content
        return PagedResponse<MutableList<SentenceEntitie>>(
            SentenceEntitieList,
            SentenceEntitiePage.number,
            SentenceEntitiePage.totalElements,
            SentenceEntitiePage.totalPages
        )
    }

    @TrackExecutionTime
    fun audioCollectionExists(audioCollectionId: String): Boolean {
        return sentenceRepository.existsById(audioCollectionId)
    }
    @TrackExecutionTime
    fun audioCollectionExistsBySentence(audioCollectionRequest: AudioCollectionRequest): Boolean {
        return sentenceRepository.existsBySentence(audioCollectionRequest.sentence)
    }
    @TrackExecutionTime
    fun sentenceExistsBySentence(sentence:String): Boolean {
        return sentenceRepository.existsBySentence(sentence)
    }

    @TrackExecutionTime
    fun getAudioCollectionById(audioCollectionId: String): SentenceEntitie {
        return sentenceRepository.findById(audioCollectionId).get()
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
    @TrackExecutionTime
    fun getAudioCollectionByLanguageId(languageId: String): Page<SentenceEntitie> {
        val paging = PageRequest.of(0, 100000, Sort.by("lastModifiedDate").descending())

        return sentenceRepository.findAllByLanguageId(languageId, paging)
    }

    @TrackExecutionTime
    fun getLanguage(languageId: String): Language? {
        return languageService.getLanguageByLanguageId(languageId)
    }

    @TrackExecutionTime
    fun languageIdExists(languageId: String): Boolean {
        return languageService.existsByLanguageId(languageId)
    }

    @TrackExecutionTime
    fun deleteAllAudioCollection() {
        sentenceRepository.deleteAll()
    }

    @TrackExecutionTime
    fun getCountOfAllAudioCollectionByLanguageId(languageId: String): Long? {
        return sentenceRepository.countAudioCollectionsByLanguageId(languageId)
    }
    @TrackExecutionTime
    fun getAllSentencesByLanguageId(
        languageId: String,
        page:Int,
        size:Int
    ): PagedResponse<MutableList<SentenceEntitie>> {

        var SentenceEntitieList = mutableListOf<SentenceEntitie>()
        val paging = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending())
        val SentenceEntitiePage: Page<SentenceEntitie> = sentenceRepository.findAllByLanguageId(languageId, paging)
        SentenceEntitieList = SentenceEntitiePage.content
        return PagedResponse<MutableList<SentenceEntitie>>(
            SentenceEntitieList,
            SentenceEntitiePage.number,
            SentenceEntitiePage.totalElements,
            SentenceEntitiePage.totalPages
        )
    }

    @TrackExecutionTime
    fun audioSentencesCount(): Long {
        return sentenceRepository.count()
    }

    @TrackExecutionTime
    fun deleteAllSentences() {
        return sentenceRepository.deleteAll()
    }

    @TrackExecutionTime
    fun getAllSentencesByBusinessId(businessId:String): List<SentenceEntitie> {
        return sentenceRepository.findAllByBusinessId(businessId)
    }

    @TrackExecutionTime
    fun getAllSentencesNotInSentenceId(page:Int,
                                       size:Int
    ): PagedResponse<MutableList<SentenceEntitie>> {
        val paging = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending())
        val sentences = sentenceRepository.findSentencesNotIn(paging)
        return PagedResponse<MutableList<SentenceEntitie>>(
            sentences.content,
            sentences.number,
            sentences.totalElements,
            sentences.totalPages
        )
    }
    @TrackExecutionTime
    fun getAllSentencesNotInSentenceIdFilterByLanguageId(
                                sentenceId:List<String>,
                                languageId: String,
                                page:Int,
                                size:Int
    ): PagedResponse<MutableList<SentenceEntitie>> {
        val paging = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending())
        val sentences = sentenceRepository.findSentencesNotInAndLanguageId(sentenceId,languageId, paging)
        return PagedResponse<MutableList<SentenceEntitie>>(
            sentences.content,
            sentences.number,
            sentences.totalElements,
            sentences.totalPages
        )
    }

    @TrackExecutionTime
    fun saveSentence(sentenceEntitie: SentenceEntitie){
        sentenceRepository.save(sentenceEntitie)
    }

    @TrackExecutionTime
    fun saveAllSentences(sentenceEntities:List<SentenceEntitie>){
        sentenceRepository.saveAll(sentenceEntities)
    }

    @TrackExecutionTime
    fun deleteAllSentencesByLanguageId(
        languageId: String
    ){
        sentenceRepository.deleteAllByLanguageId(languageId)
    }

    @TrackExecutionTime
    fun setSentenceNeedsUpload(sentenceId:String, state:Boolean){
        val sentence = getAudioCollectionById(sentenceId )
        sentence.needUploads = state
        saveSentence(sentence)
    }

    @TrackExecutionTime
    fun getSentenceById(id:String): SentenceEntitie {
        return sentenceRepository.findById(id).get()
    }
}