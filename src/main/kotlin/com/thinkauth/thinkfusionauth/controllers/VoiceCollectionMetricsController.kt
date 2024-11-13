package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.models.responses.LanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.models.responses.UserLanguageRecordingsResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics", description = "This manages voice metrics.")
class VoiceCollectionMetricsController(
    private val mediaEntityService: MediaEntityService
) {


    @Operation(
        summary = "Get all voice collections", description = "gets all voice collections", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollections")
    fun getAllVoiceCollections(
    ): ResponseEntity<MutableMap<String, Long>> {
            return ResponseEntity(mediaEntityService.countAllVoiceCollections(), HttpStatus.OK)
    }
    @Operation(
        summary = "Get count of voice collections by languageId", description = "gets all voice collections by languageId", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionsByLanguageId")
    fun getAllVoiceCollectionsByLanguageId(
        languageId: String
    ): ResponseEntity<Long> {
            return ResponseEntity(mediaEntityService.countAllVoiceCollectionsByLanguageId(languageId), HttpStatus.OK)
    }
    @Operation(
        summary = "Get count of  voice collections by acceptance state", description = "gets count of all voice collections by acceptance state", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionByAcceptanceState")
    fun getAllVoiceCollectionsByAcceptanceStateAndLanguageId(
    ): ResponseEntity<MutableMap<MediaAcceptanceState, Long>> {
        return ResponseEntity(mediaEntityService.countAllVoiceCollectionsByAcceptanceState(), HttpStatus.OK)
    }
    @Operation(
        summary = "Get count of  voice collections by acceptance state and languageId", description = "gets count of all voice collections by acceptance state and languageId", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionByAcceptanceStateAndLanguageId")
    fun getAllVoiceCollectionsByAcceptanceStateAndLanguageId(
        languageId: String
    ): ResponseEntity<MutableMap<MediaAcceptanceState, Long>> {
        return ResponseEntity(mediaEntityService.countVoiceCollectionsByAcceptanceStateAndLanguageId(languageId), HttpStatus.OK)
    }

    @Operation(
        summary = "Get count of  voice collections by logged in user", description = "gets count of all voice collections by logged in user", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionByLoggedInUser")
    fun getCountOfRecordingsByLoggedInUser(): ResponseEntity<MutableMap<String, Long>> {
        return ResponseEntity(mediaEntityService.countAllVoiceCollectionsByLoggedInUser(), HttpStatus.OK)
    }
    @Operation(
        summary = "Get count of  voice collections by logged in user", description = "gets count of all voice collections by logged in user", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionByLoggedInUserLanguage")
    fun getCountOfRecordingsByLoggedInUserLanguage(): ResponseEntity<MutableList<UserLanguageRecordingsResponse>> {
        return ResponseEntity(mediaEntityService.countAllVoiceCollectionsByLoggedInUserAndLanguage(), HttpStatus.OK)
    }
    @Operation(
        summary = "Get count of  voice collections by language", description = "gets count of all voice collections by language", tags = ["Metrics"]
    )
    @GetMapping("/countAllVoiceCollectionByLanguage")
    fun getCountOfRecordingsByLanguage(): ResponseEntity<MutableList<LanguageRecordingsResponse>> {
        return ResponseEntity(mediaEntityService.countAllByLanguages(), HttpStatus.OK)
    }
    @Operation(
        summary = "Get all voice collections by logged in user", description = "gets all voice collections by logged in user", tags = ["Metrics"]
    )
    @GetMapping("/allVoiceCollectionByLoggedInUser")
    fun getAllRecordingsByLoggedInUser(
        @RequestParam(name = "page", defaultValue = "0") page:Int= 0,
        @RequestParam(name = "size", defaultValue = "100") size:Int = 0,
    ): ResponseEntity<PagedResponse<List<MediaEntity>>> {
        return ResponseEntity(mediaEntityService.findAllVoiceCollectionsByLoggedInUser(page,size), HttpStatus.OK)
    }
    @Operation(
        summary = "Get all voice collections by logged in user and language id", description = "gets all voice collections by logged in user and language id", tags = ["Metrics"]
    )
    @GetMapping("/allVoiceCollectionByLoggedInUserLanguageId")
    fun getAllRecordingsByLoggedInUserLanguageId(
        @RequestParam(name = "languageId")languageId:String,
        @RequestParam(name = "page", defaultValue = "0") page:Int= 0,
        @RequestParam(name = "size", defaultValue = "100") size:Int = 0,
    ): ResponseEntity<PagedResponse<List<MediaEntity>>> {
        return ResponseEntity(mediaEntityService.findAllVoiceCollectionsByLoggedInUserLanguageId(languageId,page,size), HttpStatus.OK)
    }
//
//    @Operation(
//        summary = "Get a media entities", description = "gets a media entity", tags = ["MediaEntities"]
//    )
//    @GetMapping("/mediaEntity")
//    fun getSingleMediaEntity(
//       @RequestParam("mediaEntityId") mediaEntityid:String
//    ): ResponseEntity<MediaEntity> {
//            return ResponseEntity(mediaEntityService.fetchMediaEntityById(mediaEntityid), HttpStatus.OK)
//    }
//    @Operation(
//        summary = "Get a media entity by user", description = "gets a media entity by User", tags = ["MediaEntities"]
//    )
//    @GetMapping("/mediaEntityByUser")
//    fun getByUser(
//        @RequestParam("email") email:String
//    ): ResponseEntity<List<MediaEntity>> {
//        return ResponseEntity(mediaEntityService.fetchAllMediaEntityByUser(email),HttpStatus.OK)
//    }
//
//    @Operation(
//        summary = "Get a media entity by sentenceId", description = "gets a media entity by SentenceId", tags = ["MediaEntities"]
//    )
//    @GetMapping("/mediaEntityBySentenceId")
//    fun getBySentenceId(
//        @RequestParam("sentenceId") sentenceId:String
//    ): ResponseEntity<List<MediaEntity>> {
//        return ResponseEntity(mediaEntityService.fetchAllMediaEntityBySentenceId(sentenceId),HttpStatus.OK)
//    }
//
//    @Operation(
//        summary = "Get a media entity by businessId", description = "gets a media entity by BusinessId", tags = ["MediaEntities"]
//    )
//    @GetMapping("/mediaEntityByBusinessId")
//    fun getByBusinessId(
//        @RequestParam("businessId") businessId:String
//    ): ResponseEntity<List<MediaEntity>> {
//        return ResponseEntity(mediaEntityService.fetchAllMediaEntityByBusinessId(businessId),HttpStatus.OK)
//    }
//
//    @Operation(
//        summary = "Get media entities by acceptedState", description = "gets media entities by AcceptedStates", tags = ["MediaEntities"]
//    )
//    @GetMapping("/mediaEntityByAcceptedState")
//    fun getByAcceptedState(
//        @RequestParam("acceptedState") acceptedState:Boolean
//    ): ResponseEntity<List<MediaEntity>>{
//        return ResponseEntity(mediaEntityService.fetchMediaEntitiesByAcceptedState(acceptedState),HttpStatus.OK)
//    }
//    @Operation(
//        summary = "Assign Media entity as accepted", description = "assign media entity as accepted", tags = ["MediaEntities"]
//    )
//    @PutMapping("/mediaEntityAccept")
//    fun assignAccepted(
//        @RequestParam("mediaEntityId") sentenceId:String
//    ): ResponseEntity<MediaEntity>{
//        return ResponseEntity(mediaEntityService.acceptMediaEntity(sentenceId),HttpStatus.OK)
//    }
//    @Operation(
//        summary = "Assign Media entity as rejected", description = "assign media entity as rejected", tags = ["MediaEntities"]
//    )
//    @PutMapping("/mediaEntityReject")
//    fun assignRejected(
//        @RequestParam("mediaEntityId") sentenceId:String
//    ): ResponseEntity<MediaEntity>{
//        return ResponseEntity(mediaEntityService.rejectMediaEntity(sentenceId),HttpStatus.OK)
//    }
}