package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.entities.AudioCollection
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.AudioCollectionRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.name


@Service
class AudioCollectionService(
    private val audioRepository: AudioCollectionRepository,
    private val languageService: LanguageService,
    private val fileProcessingHelper: FileProcessingHelper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionService::class.java)
    fun addSentenceCollection(sentenceId:String, file: MultipartFile): AudioCollection {
        val sentence = audioRepository.findById(sentenceId).get();
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION,file.originalFilename!!)
        sentence.audio = path.toString()

        val finalCollection = audioRepository.save(sentence)

        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(file, path, BucketName.VOICE_COLLECTION)
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)

        return finalCollection
    }

    fun addAudioEvent(
        file: MultipartFile, audioCollection: AudioCollection
    ): AudioCollection {
        val path = fileProcessingHelper.mediaFullPath(BucketName.VOICE_COLLECTION, file.name)
        LOGGER.info("file_path_add_audio: "+ path.toString())
        audioCollection.audio = path
        val finalCollection = audioRepository.save(audioCollection)
        val onMediaUploadAudioCollectionEvent = OnMediaUploadItemEvent(file, path, BucketName.VOICE_COLLECTION)
        applicationEventPublisher.publishEvent(onMediaUploadAudioCollectionEvent)
        LOGGER.info("finalCollection: "+ finalCollection.toString())
        return finalCollection
    }

    fun addSentenceCollection(audioCollectionRequest: AudioCollectionRequest): AudioCollection {
        val language = languageService.getLanguageByLanguageId(audioCollectionRequest.languageId)
        val collection = AudioCollection(
            sentence = audioCollectionRequest.sentence,
            language = language!!,
            englishTranslation = audioCollectionRequest.englishTranslation
        )
        return audioRepository.save(collection)
    }

    fun getAllAudioCollection(
        page: Int, size: Int
    ): PagedResponse<MutableList<AudioCollection>> {
        var audioCollectionList = mutableListOf<AudioCollection>()
        val paging = PageRequest.of(page, size)
        val audioCollectionPage: Page<AudioCollection> = audioRepository.findAll(paging)
        audioCollectionList = audioCollectionPage.content
        return PagedResponse<MutableList<AudioCollection>>(
            audioCollectionList,
            audioCollectionPage.number,
            audioCollectionPage.totalElements,
            audioCollectionPage.totalPages
        )
    }

    fun audioCollectionExists(audioCollectionId: String): Boolean {
        return audioRepository.existsById(audioCollectionId)
    }

    fun audioCollectionExistsBySentence(audioCollectionRequest: AudioCollectionRequest): Boolean {
        return audioRepository.existsBySentence(audioCollectionRequest.sentence)
    }

    fun getAudioCollectionById(audioCollectionId: String): AudioCollection {
        return audioRepository.findById(audioCollectionId).get()
    }

    fun getMinioObject(){

    }
//    fun getMinioObject(audioCollectionId: String, response: HttpServletResponse){
//        val audioCollection = audioRepository.findById(audioCollectionId).get()
//        val fullPath = storageService.getMediaFullPath(ResourceType.VOICE_COLLECTION,audioCollection.audio!!)
//        val inputStream: InputStream = minioService.get(Path(fullPath.pathString))
//        val inputStreamResource = InputStreamResource(inputStream)
//
//        // Set the content type and attachment header.
//
//        // Set the content type and attachment header.
//        response.addHeader("Content-disposition", "attachment;filename=${audioCollection.audio}")
//        response.contentType = URLConnection.guessContentTypeFromName(audioCollection.audio)
//
//        // Copy the stream to the response's output stream.
//
//        // Copy the stream to the response's output stream.
//        IOUtils.copy(inputStream, response.outputStream)
//        response.flushBuffer()
//    }

    fun getAudioCollectionByLanguageId(languageId: String): List<AudioCollection> {
        return audioRepository.findAllByLanguageId(languageId)
    }

    fun getAudioCollectionByLanguageWithNoAudio(languageId: String): List<AudioCollection> {

        return audioRepository.findAllByAudioIsNullAndLanguageId(languageId)
    }

    fun getAudioCollectionByLanguageWithNoAudioCount(languageId: String): Long? {

        return audioRepository.countAudioCollectionsByLanguageIdAndAudioIsNull(languageId)
    }

    fun getAudioCollectionByLanguageWithAudio(languageId: String): List<AudioCollection> {

        return audioRepository.findAudioCollectionsByLanguageIdAndAudioIsNotNull(languageId)
    }

    fun getAudioCollectionByLanguageWithAudioCount(languageId: String): Long? {

        return audioRepository.countAudioCollectionsByLanguageIdAndAudioIsNotNull(languageId)
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

    fun getCountOfAllAudioCollectionByLanguageIdAndNoAudioUrl(languageId: String): Long? {
        return audioRepository.countAudioCollectionsByLanguageIdAndAudioIsNotNull(languageId)
    }

    fun audioSentencesCount(): Long {
        return audioRepository.count()
    }

    fun deleteAllSentences() {
        return audioRepository.deleteAll()
    }
}