package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.SentenceEntity
import com.thinkauth.thinkfusionauth.entities.SentenceUploadEntity
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import com.thinkauth.thinkfusionauth.services.SentenceUploadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val uploadService: SentenceUploadService
) {
    private val LOGGER:Logger = LoggerFactory.getLogger(AudioCollectionController::class.java)
    @Operation(
        summary = "Add an audio", description = "adds an audio to an existing sentence", tags = ["Audio"]
    )
    @PostMapping(
        value = ["/addAudioCollection"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun addAudioCollection(
        @RequestParam sentenceId:String,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<SentenceUploadEntity> {
//        val audioCollectionRequest = AudioCollectionRequest(sentence,language)
        if(audioCollectionService.audioCollectionExists(sentenceId)){

            return ResponseEntity(uploadService.addSentenceUpload(sentenceId,file), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
//    @Operation(
//        summary = "Add an audio by Language Id", description = "adds an audio by Language Id", tags = ["Audio"]
//    )
//    @PostMapping(
//        value = ["/addAudioByLanguageId"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
//    )
//    @PreAuthorize("hasRole('ADMIN')")
//    fun addAudioCollectionByLanguageId(
//        @RequestParam sentence:String,
//        @RequestParam languageId:String,
//        @RequestPart("file") file: MultipartFile
//    ): ResponseEntity<Any> {
//        if(audioCollectionService.languageIdExists(languageId)) {
//            val languageEntity = audioCollectionService.getLanguage(languageId)
//            val audioCollectionRequest = AudioCollectionRequest(sentence, languageEntity?.languageName!!)
//            if (!audioCollectionService.audioCollectionExistsBySentence(audioCollectionRequest)) {
//
//                return ResponseEntity(
//                    audioCollectionService.addSentenceCollection(audioCollectionRequest, file),
//                    HttpStatus.OK
//                )
//            }
//            return ResponseEntity(HttpStatus.CONFLICT)
//        }
//        return ResponseEntity(GenericResponse("language not found"),HttpStatus.NOT_FOUND)
//    }

    @Operation(
        summary = "Add an audio by Collection Id", description = "adds an audio by Collection Id", tags = ["Audio"]
    )
    @PostMapping(
        value = ["/addAudioByCollectionId/{sentenceId}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    fun addAudioToCollection(
        @PathVariable("sentenceId") audioCollectionId:String,
        @RequestPart("file") file: MultipartFile
    ):ResponseEntity<SentenceUploadEntity>{
        if(audioCollectionService.audioCollectionExists(audioCollectionId)){
            LOGGER.info("sentence exists")
            val audioCollection = audioCollectionService.getAudioCollectionById(audioCollectionId)
            LOGGER.info("audioCollectionFound: "+ audioCollection.toString())

            return ResponseEntity(uploadService.addAudioEvent(file,audioCollection),HttpStatus.OK)
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
       @RequestBody audioRequest:AudioCollectionRequest
    ):ResponseEntity<SentenceEntity>{
        if(audioCollectionService.languageIdExists(audioRequest.languageId)) {

            if (!audioCollectionService.audioCollectionExistsBySentence(audioRequest)) {
                return ResponseEntity(
                    audioCollectionService.addSentenceCollection(audioRequest),
                    HttpStatus.OK
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
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<MutableList<SentenceEntity>>> {
        return ResponseEntity(audioCollectionService.getAllSentences(page, size),HttpStatus.OK)
    }
    @Operation(
        summary = "Get all sentences", description = "gets all sentences", tags = ["Audio"]
    )
    @GetMapping("/sentencesByBusinessId")
    fun getSentencesByBusinessId(
        @RequestParam("businessId") businessId:String
    ): ResponseEntity<List<SentenceEntity>> {
        return ResponseEntity(audioCollectionService.getAllSentencesByBusinessId(businessId),HttpStatus.OK)
    }

//    @Operation(
//        summary = "Get single audio object", description = "gets a single audio object", tags = ["Audio"]
//    )
//    @GetMapping("/audioObject/{audioId}")
//    fun getAudioObject(
//        @PathVariable("audioId") audioId:String,
//        response:HttpServletResponse
//    ){
//
//        return audioCollectionService.get(audioId,response)
//    }

    @Operation(
        summary = "Get single audio collection", description = "gets a single audio", tags = ["Audio"]
    )
    @GetMapping("/audioCollection/{audioId}")
    fun getAudio(
        @PathVariable("audioId") audioId:String,
        response:HttpServletResponse
    ): SentenceEntity {

        return audioCollectionService.getAudioCollectionById(audioId)
    }
    @Operation(
        summary = "Get all sentences by languageId", description = "gets all audios by languageId", tags = ["Audio"]
    )
    @GetMapping("/audioCollectionByLanguageId/{languageId}")
    fun getAllAudioByLanguageId(
        @PathVariable("languageId") languageId:String
    ): List<SentenceEntity> {

        return audioCollectionService.getAudioCollectionByLanguageId(languageId)
    }

//    @Operation(
//        summary = "Get single audio by languageId", description = "gets a single audio by languageId", tags = ["Audio"]
//    )
//    @GetMapping("/audioByLanguage/{languageId}")
//    fun getAudioByLanguageId(
//        @PathVariable("languageId") languageId:String,
//        response:HttpServletResponse
//    ): List<AudioCollection> {
//
//        return audioCollectionService.getAudioCollectionByLanguageId(languageId)
//    }
//    @Operation(
//        summary = "Get audio by languageId and no audio", description = "gets audio by languageId and no audio", tags = ["Audio"]
//    )
//    @GetMapping("/audioByLanguageAndNoAudio/{languageId}")
//    fun getAudioByLanguageIdAndNoAudio(
//        @PathVariable("languageId") languageId:String,
//    ): List<SentenceEntity> {
//
//        return audioCollectionService.getAudioCollectionByLanguageWithNoAudio(languageId)
//    }

//    @Operation(
//        summary = "Get audio by languageId and with audio", description = "gets audio by languageId and with audio", tags = ["Audio"]
//    )
//    @GetMapping("/audioByLanguageAndWithAudio/{languageId}")
//    fun getAudioByLanguageIdAndWithAudio(
//        @PathVariable("languageId") languageId:String,
//    ): List<SentenceEntity> {
//
//        return audioCollectionService.getAudioCollectionByLanguageWithAudio(languageId)
//    }

    @Operation(
        summary = "Deletes All Audio Collections", description = "deletes all audio collections", tags = ["Audio"]
    )
    @DeleteMapping("/deleteAll")
    fun deleteAllAudioCollection(){
        return audioCollectionService.deleteAllAudioCollection()
    }
}