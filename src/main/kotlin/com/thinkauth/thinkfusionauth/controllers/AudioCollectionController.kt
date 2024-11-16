package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.SentenceEntitie
import com.thinkauth.thinkfusionauth.entities.SentenceUploadEntity
import com.thinkauth.thinkfusionauth.entities.SentenceUserIgnore
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.SentenceUploadService
import com.thinkauth.thinkfusionauth.services.SentenceUserIgnoreService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/audioCollection")
@Tag(name = "Audio", description = "This manages the audio in the system.")
class AudioCollectionController(
    private val audioCollectionService: AudioCollectionService,
    private val uploadService: SentenceUploadService,
    private val sentenceUserIgnoreService: SentenceUserIgnoreService,
    private val userManagementService: UserManagementService,
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionController::class.java)

    @Operation(
        summary = "Add an audio", description = "adds an audio to an existing sentence", tags = ["Audio"]
    )
    @PostMapping(
        value = ["/addAudioCollection"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun addAudioCollection(
        @RequestParam sentenceId: String, @RequestPart("file") file: MultipartFile
    ): ResponseEntity<SentenceUploadEntity> {
//        val audioCollectionRequest = AudioCollectionRequest(sentence,language)
        if (audioCollectionService.audioCollectionExists(sentenceId)) {

            return ResponseEntity(uploadService.addSentenceUpload(sentenceId, file), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "Add an audio by Collection Id", description = "adds an audio by Collection Id", tags = ["Audio"]
    )
    @PostMapping(
        value = ["/addAudioByCollectionId/{sentenceId}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    fun addAudioToCollection(
        @PathVariable("sentenceId") audioCollectionId: String, @RequestPart("file") file: MultipartFile
    ): ResponseEntity<SentenceUploadEntity> {
        if (audioCollectionService.audioCollectionExists(audioCollectionId)) {
            LOGGER.info("sentence exists")
            val audioCollection = audioCollectionService.getAudioCollectionById(audioCollectionId)
            LOGGER.info("audioCollectionFound: " + audioCollection.toString())

            return ResponseEntity(uploadService.addAudioEvent(file, audioCollection), HttpStatus.OK)
        }
        LOGGER.info("sentence doesn't exist")
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "Add a sentence without audio", description = "adds a sentence without audio", tags = ["Audio"]
    )
    @PostMapping(
        value = ["/addSentence"]
    )
    @PreAuthorize("hasRole('ADMIN')")
    fun addSentenceWithoutAudio(
        @RequestBody audioRequest: AudioCollectionRequest
    ): ResponseEntity<SentenceEntitie> {
        if (audioCollectionService.languageIdExists(audioRequest.languageId)) {

            if (!audioCollectionService.audioCollectionExistsBySentence(audioRequest)) {
                return ResponseEntity(
                    audioCollectionService.addSentenceCollection(audioRequest), HttpStatus.OK
                )
            }
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "Get all sentences", description = "gets all sentences", tags = ["Audio"]
    )
    @GetMapping("/sentences")
    fun getSentencesPaged(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<SentenceEntitie>>> {
        return ResponseEntity(audioCollectionService.getAllSentences(page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Get all sentences filtered", description = "gets all sentences filtered", tags = ["Audio"]
    )
    @GetMapping("/sentencesFiltered")
    fun getSentencesFilteredPaged(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<SentenceEntitie>>> {

        return ResponseEntity(
            audioCollectionService.getAllSentencesNotInSentenceId( page, size),
            HttpStatus.OK
        )
    }

    @Operation(
        summary = "Get all sentences filtered by ignore and languageId",
        description = "gets all sentences filtered by ignore and languageId",
        tags = ["Audio"]
    )
    @GetMapping("/sentencesFilteredByLanguageId")
    fun getSentencesFilteredByLanguageIdPaged(
        @RequestParam("languageId") languageId: String,
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<SentenceEntitie>>> {

        return ResponseEntity(
            audioCollectionService.getAllSentencesNotInSentenceIdFilterByLanguageId(
                languageId,
                page,
                size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Ignore Sentence", description = "Ignores a sentence", tags = ["Audio"]
    )
    @PostMapping("/ignoreSentence")
    fun ignoreSentence(
        @RequestParam("sentenceId") sentenceId: String
    ): ResponseEntity<SentenceUserIgnore> {
        val userId = userManagementService.loggedInUser()!!
        return ResponseEntity(sentenceUserIgnoreService.addSentenceUserIgnore(userId, sentenceId), HttpStatus.OK)
    }

    @Operation(
        summary = "Ignore Sentence by passing both user id and sentence id",
        description = "Ignores a sentence by passing both user id and sentence id",
        tags = ["Audio"]
    )
    @PostMapping("/ignoreSentenceExplicit")
    fun ignoreSentenceExplicit(
        @RequestParam("userId") userId: String, @RequestParam("sentenceId") sentenceId: String
    ): ResponseEntity<SentenceUserIgnore> {

        return ResponseEntity(sentenceUserIgnoreService.addSentenceUserIgnore(userId, sentenceId), HttpStatus.OK)
    }

    @Operation(
        summary = "UnIgnore Sentence by passing both user id and sentence id",
        description = "UnIgnores a sentence by passing both user id and sentence id",
        tags = ["Audio"]
    )
    @DeleteMapping("/unIgnoreSentence")
    fun unIgnoreSentenceExplicit(
        @RequestParam("userId") userId: String, @RequestParam("sentenceId") sentenceId: String
    ): ResponseEntity<Unit> {

        return ResponseEntity(sentenceUserIgnoreService.removeSentenceUserIgnore(userId, sentenceId), HttpStatus.OK)
    }

    @Operation(
        summary = "Get all sentences", description = "gets all sentences", tags = ["Audio"]
    )
    @GetMapping("/sentencesByBusinessId")
    fun getSentencesByBusinessId(
        @RequestParam("businessId") businessId: String
    ): ResponseEntity<List<SentenceEntitie>> {
        return ResponseEntity(audioCollectionService.getAllSentencesByBusinessId(businessId), HttpStatus.OK)
    }

    @Operation(
        summary = "Get single audio collection", description = "gets a single audio", tags = ["Audio"]
    )
    @GetMapping("/audioCollection/{audioId}")
    fun getAudio(
        @PathVariable("audioId") audioId: String, response: HttpServletResponse
    ): SentenceEntitie {

        return audioCollectionService.getAudioCollectionById(audioId)
    }

    @Operation(
        summary = "Get all sentences by languageId", description = "gets all audios by languageId", tags = ["Audio"]
    )
    @GetMapping("/audioCollectionByLanguageId/{languageId}")
    fun getAllAudioByLanguageId(
        @PathVariable("languageId") languageId: String
    ): Page<SentenceEntitie> {

        return audioCollectionService.getAudioCollectionByLanguageId(languageId,0, 1000)
    }

    @Operation(
        summary = "Get all sentences by languageId", description = "gets all audios by languageId", tags = ["Audio"]
    )
    @GetMapping("/audioCollectionByLanguageIdPaged/{languageId}")
    fun getAllAudioByLanguageIdPaged(
        @PathVariable("languageId") languageId: String,
        @RequestParam(name = "page", defaultValue = "0") page:Int= 0,
    @RequestParam(name = "size", defaultValue = "100") size:Int = 0
    ): Page<SentenceEntitie> {

        return audioCollectionService.getAudioCollectionByLanguageId(languageId,page, size)
    }

    @Operation(
        summary = "Get single audio object", description = "gets a single audio object", tags = ["Audio"]
    )
    @GetMapping("/audioObject")
    fun getAudioObject(
        @RequestParam("audioName") audioName: String, response: HttpServletResponse
    ) {

        return audioCollectionService.getMinioObject(audioName, response)
    }

    @Operation(
        summary = "fetch sentence by Id",
        description = "fetches a sentence by id",
        tags = ["Audio"]
    )
    @GetMapping("/sentenceById")
    fun getSentenceById(
        @RequestParam("sentenceId") sentenceId: String,
    ): ResponseEntity<SentenceEntitie> {
        return ResponseEntity(audioCollectionService.getSentenceById(sentenceId), HttpStatus.OK)
    }


    @Operation(
        summary = "Deletes All Audio Collections by languageId",
        description = "deletes all audio collections by languageId",
        tags = ["Sentences"]
    )
    @DeleteMapping("/deleteByLanguageId")
    fun deleteByLanguageId(
        @RequestParam("languageId") languageId: String
    ) {
        return audioCollectionService.deleteAllSentencesByLanguageId(languageId)
    }
}