package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.SentenceDocumentEntity
import com.thinkauth.thinkfusionauth.entities.SentenceEntity
import com.thinkauth.thinkfusionauth.models.requests.AudioCollectionRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.SentenceDocumentImpl
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
    private val audioCollectionService: AudioCollectionService,
    private val sentenceDocumentImpl: SentenceDocumentImpl
)  {

    private val LOGGER: Logger = LoggerFactory.getLogger(AudioCollectionController::class.java)
    @Operation(
        summary = "Add a sentence", description = "adds a sentence", tags = ["Sentences"]
    )
    @PostMapping("/addSentence")
    fun addSentenceWithoutAudio(
        @RequestBody audioRequest: AudioCollectionRequest
    ):ResponseEntity<SentenceEntity>{
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
    ): ResponseEntity<PagedResponse<MutableList<SentenceEntity>>> {
        return ResponseEntity(audioCollectionService.getAllSentences(page, size),HttpStatus.OK)
    }

    @Operation(
        summary = "Upload sentence document", description = "Update a document for sentence", tags = ["Sentences"]
    )
    @PostMapping(
        value = ["/uploadSentenceDocument"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun addSentenceDocument(
        @RequestPart("file") file: MultipartFile,
        @RequestParam("languageId") languageId:String,
        @RequestParam("dialectId") dialectId:String,

    ): ResponseEntity<SentenceDocumentEntity> {
        return ResponseEntity(sentenceDocumentImpl.addDBFile(
            languageId = languageId,
            dialectId = dialectId,
            upload = file
        ),HttpStatus.OK)
    }

    @Operation(
        summary = "Get all sentence documents", description = "gets all sentence documents", tags = ["Sentences"]
    )
    @GetMapping("/sentenceDocuments")
    fun getSentenceDocuments(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<SentenceDocumentEntity>>> {
        return ResponseEntity(sentenceDocumentImpl.findEverythingPaged(page, size),HttpStatus.OK)
    }

    @Operation(
        summary = "Delete all sentence documents", description = "Delete all sentence documents", tags = ["Sentences"]
    )
    @DeleteMapping("/deleteAllSentenceDocuments")
    fun deleteAllSentenceDocuments(){
        sentenceDocumentImpl.deleteAllItems()
    }
}