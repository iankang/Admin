package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import com.thinkauth.thinkfusionauth.services.MediaEntityUserUploadStateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mediaEntitiesUploadState")
@Tag(name = "MediaEntitiesUploadState", description = "This manages media entities.")
class MediaEntityUploadStateController(
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService,
    private val mediaEntityService: MediaEntityService
) {

    @Operation(
        summary = "Get all media entities upload states", description = "gets all media entities upload states", tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/allMediaEntitiesUploadState")
    fun getAllMediaEntitiesUploadState(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<MediaEntityUserUploadState>>> {
        return ResponseEntity(mediaEntityUserUploadStateService.getAllMediaEntityUploadState(page, size), HttpStatus.OK)
    }
    @Operation(
        summary = "Get all media entities upload states by languageId", description = "gets all media entities upload states by language Id", tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/allMediaEntitiesUploadStateByLanguageId")
    fun getAllMediaEntitiesUploadStateByLanguageId(
        @RequestParam("languageId", required = true) languageId: String,
        @RequestParam("mediaAcceptanceState", required = false) mediaAcceptanceState: MediaAcceptanceState?,
        @RequestParam("paymentState", required = false) paymentState: PaymentState?,
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserUploadState>>> {
        return ResponseEntity(mediaEntityUserUploadStateService.getAllByLanguageIdAndMediaAcceptanceAndPaymentState(languageId, mediaAcceptanceState, paymentState, page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Mark an entity as paid by mediaEntityId", description = "marks an entity as paid by mediaEntityId", tags = ["MediaEntitiesUploadState"]
    )
    @PostMapping("/payByMediaEntityId")
    fun payByMediaEntityId(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
    ): ResponseEntity<MediaEntityUserUploadState> {
        return ResponseEntity(mediaEntityUserUploadStateService.makePayment(mediaEntityId),HttpStatus.OK)
    }

}