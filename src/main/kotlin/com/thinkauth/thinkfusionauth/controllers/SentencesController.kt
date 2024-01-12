package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.AudioCollection
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.AudioCollectionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/sentences")
@Tag(name = "Sentences", description = "This manages the sentences in the system.")
class SentencesController(
    private val audioCollectionService: AudioCollectionService
)  {

    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionController::class.java)
    @Operation(
        summary = "Add a sentence", description = "adds a sentence", tags = ["Sentences"]
    )
    @PostMapping(
        value = ["/addSentence"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun addSentenceWithoutAudio(
        @RequestBody audioRequest: AudioCollectionRequest
    ):ResponseEntity<AudioCollection>{
        if(audioCollectionService.languageIdExists(audioRequest.languageId)) {
            LOGGER.info("language Exists")
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
        summary = "Get all sentences", description = "gets all sentences", tags = ["Sentences"]
    )
    @GetMapping("/sentences")
    fun getAudios(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<MutableList<AudioCollection>>> {
        return ResponseEntity(audioCollectionService.getAllAudioCollection(page, size),HttpStatus.OK)
    }
}