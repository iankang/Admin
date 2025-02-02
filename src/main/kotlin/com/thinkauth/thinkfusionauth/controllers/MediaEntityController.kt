package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.models.responses.DurationLanguageSum
import com.thinkauth.thinkfusionauth.models.responses.DurationSum
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mediaEntities")
@Tag(name = "MediaEntities", description = "This manages media entities.")
class MediaEntityController(
    private val mediaEntityService: MediaEntityService
) {

    @Operation(
        summary = "Get all media entities", description = "gets all media entities", tags = ["MediaEntities"]
    )
    @GetMapping("/allMediaEntities")
    fun getAllMediaEntities(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<MediaEntity>>> {
            return ResponseEntity(mediaEntityService.fetchAllMediaEntityPaged(page, size), HttpStatus.OK)
    }
    @Operation(
        summary = "Get all media entities by languageId", description = "gets all media entities by languageId", tags = ["MediaEntities"]
    )
    @GetMapping("/allMediaEntitiesByLanguageId")
    fun getAllMediaEntitiesByLanguageId(
        @RequestParam("languageId") languageId:String,
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<MediaEntity>>> {
            return ResponseEntity(mediaEntityService.fetchAllMediaEntityPagedByLanguageId(languageId,page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Get a media entity", description = "gets a media entity", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntity")
    fun getSingleMediaEntity(
       @RequestParam("mediaEntityId") mediaEntityid:String
    ): ResponseEntity<MediaEntity> {
            return ResponseEntity(mediaEntityService.fetchMediaEntityById(mediaEntityid), HttpStatus.OK)
    }
    @Operation(
        summary = "Get a media entity by user", description = "gets a media entity by User", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityByUser")
    fun getByUser(
        @RequestParam("email") email:String
    ): ResponseEntity<List<MediaEntity>> {
        return ResponseEntity(mediaEntityService.fetchAllMediaEntityByUser(email),HttpStatus.OK)
    }

    @Operation(
        summary = "Get a media entity by sentenceId", description = "gets a media entity by SentenceId", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityBySentenceId")
    fun getBySentenceId(
        @RequestParam("sentenceId") sentenceId:String
    ): ResponseEntity<List<MediaEntity>> {
        return ResponseEntity(mediaEntityService.fetchAllMediaEntityBySentenceId(sentenceId),HttpStatus.OK)
    }

    @Operation(
        summary = "Get a media entity by businessId", description = "gets a media entity by BusinessId", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityByBusinessId")
    fun getByBusinessId(
        @RequestParam("businessId") businessId:String
    ): ResponseEntity<List<MediaEntity>> {
        return ResponseEntity(mediaEntityService.fetchAllMediaEntityByBusinessId(businessId),HttpStatus.OK)
    }

    @Operation(
        summary = "Assign Media entity as accepted", description = "assign media entity as accepted", tags = ["MediaEntities"]
    )
    @PutMapping("/mediaEntityAccept")
    fun assignAccepted(
        @RequestParam("mediaEntityId") sentenceId:String
    ): ResponseEntity<MediaEntity>{
        return ResponseEntity(mediaEntityService.acceptMediaEntity(sentenceId),HttpStatus.OK)
    }
    @Operation(
        summary = "Assign Media entity as rejected", description = "assign media entity as rejected", tags = ["MediaEntities"]
    )
    @PutMapping("/mediaEntityReject")
    fun assignRejected(
        @RequestParam("mediaEntityId") sentenceId:String,
        @RequestBody(required = false) rejectionReason:String?
    ): ResponseEntity<MediaEntity>{
        return ResponseEntity(mediaEntityService.rejectMediaEntity(sentenceId,rejectionReason),HttpStatus.OK)
    }

    @Operation(
        summary = "Check if a media entity exists for a sentenceId ", description = "Checks if a media entity exists for a sentenceId", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityExistsForSentence")
    fun mediaEntityExistsForSentence(
        @RequestParam("sentenceId") sentenceId: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity(mediaEntityService.mediaEntityForSentenceExists(sentenceId),HttpStatus.OK)
    }

    @Operation(
        summary = "Get media by acceptance state ", description = "Get media by acceptance State ", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityByAcceptanceState")
    fun mediaEntityByAcceptanceState(
        @RequestParam("mediaState") mediaAcceptanceState: MediaAcceptanceState,
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntity>>> {
        return ResponseEntity(mediaEntityService.findMediaEntitiesByStatus(mediaAcceptanceState, page, size),HttpStatus.OK)
    }

    @Operation(
        summary = "Get media by acceptance state ", description = "Get media by acceptance State ", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityByAcceptanceStateAndLanguageId")
    fun mediaEntityByAcceptanceStateAndLanguageId(
        @RequestParam("mediaState") mediaAcceptanceState: MediaAcceptanceState,
        @RequestParam("languageId") languageId: String,
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntity>>> {
        return ResponseEntity(mediaEntityService.findMediaEntitiesByMediaAcceptanceStateAndLanguageId(mediaAcceptanceState, languageId,page, size),HttpStatus.OK)
    }

    @Operation(
        summary = "Get media duration ", description = "Get media duration ", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityDuration")
    fun mediaEntityGetDuration(
        @RequestParam("mediaName") mediaName:String
    ): ResponseEntity<Float> {
        return ResponseEntity(mediaEntityService.mediaEntityGetDuration(mediaName), HttpStatus.OK)
    }

    @Operation(
        summary = "Get media aggregated ", description = "Get media aggregated ", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityAggregate")
    fun mediaEntityAggregated(): MutableList<DurationSum> {
       return mediaEntityService.aggregateMediaEntities()
    }
    @Operation(
        summary = "Get media Language aggregated ", description = "Get media aggregated language", tags = ["MediaEntities"]
    )
    @GetMapping("/mediaEntityLanguageAggregate")
    fun mediaEntityLanguageAggregated(): MutableList<DurationLanguageSum> {
       return mediaEntityService.aggregateLanguageHoursMediaEntities()
    }
}
