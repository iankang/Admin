package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import com.thinkauth.thinkfusionauth.services.MediaEntityUserUploadStateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mediaEntitiesUploadState")
@Tag(name = "MediaEntitiesUploadState", description = "This manages media entities.")
class MediaEntityUploadStateController(
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService,
    private val mediaEntityService: MediaEntityService
) {

    @Operation(
        summary = "Get all media entities upload states",
        description = "gets all media entities upload states",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/allMediaEntitiesUploadState")
    fun getAllMediaEntitiesUploadState(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<List<MediaEntityUserUploadState>>> {
        return ResponseEntity(mediaEntityUserUploadStateService.getAllMediaEntityUploadState(page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Get all media entities upload states by languageId",
        description = "gets all media entities upload states by language Id",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/allMediaEntitiesUploadStateByLanguageId")
    fun getAllMediaEntitiesUploadStateByLanguageId(
        @RequestParam("languageId", required = true) languageId: String,
        @RequestParam("mediaAcceptanceState", required = false) mediaAcceptanceState: MediaAcceptanceState?,
        @RequestParam("paymentState", required = false) paymentState: PaymentState?,
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserUploadState>>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByLanguageIdAndMediaAcceptanceAndPaymentState(
                languageId, mediaAcceptanceState, paymentState, page, size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Mark an entity as paid by mediaEntityId",
        description = "marks an entity as paid by mediaEntityId",
        tags = ["MediaEntitiesUploadState"]
    )
    @PostMapping("/payByMediaEntityId")
    fun payByMediaEntityId(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
    ): ResponseEntity<MediaEntityUserUploadState> {
        return ResponseEntity(mediaEntityUserUploadStateService.makePayment(mediaEntityId), HttpStatus.OK)
    }

    @Operation(
        summary = "Get all by uploadDate",
        description = "marks an entity as paid by mediaEntityId",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/byUploadDate")
    fun getAllByUploadDate(
        @RequestParam(
            "uploadDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDate: LocalDate,
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserUploadState>>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByUploadDate(
                uploadDate.atStartOfDay(),
                page,
                size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Get all by uploadDate and mediaState",
        description = "gets all by uploadDate and mediaState",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/byUploadDateAndMediaState")
    fun getAllByUploadDateAndMediaState(
        @RequestParam(
            "uploadDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDate: LocalDate,
        @RequestParam("mediaState", required = true) mediaState: MediaAcceptanceState,
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserUploadState>>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByUploadDateAndMediaState(
                uploadDate.atStartOfDay(),
                mediaState,
                page,
                size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Get all by uploadDate and payment state",
        description = "gets all by uploadDate and payment state",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/byUploadDateAndPaymentState")
    fun getAllByUploadDateAndPaymentState(
        @RequestParam(
            "uploadDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDate: LocalDate,
        @RequestParam("paymentState", required = true) paymentState: PaymentState?,
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserUploadState>>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByUploadDateAndPaymentState(
                uploadDate.atStartOfDay(),
                paymentState,
                page,
                size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Gets all greater than uploadDate",
        description = "Gets all greater than uploadDate",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/greaterThanUploadDate")
    fun getAllGreaterThanUploadDate(
        @RequestParam(
            "uploadDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDate: LocalDate
    ): ResponseEntity<List<MediaEntityUserUploadState>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllGreaterThanUploadDate(uploadDate.atStartOfDay()),
            HttpStatus.OK
        )
    }

    @Operation(
        summary = "Gets all greater than uploadDate",
        description = "Gets all greater than uploadDate",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/uploadDateRange")
    fun getAllWithinUploadRange(
        @RequestParam(
            "uploadDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDateStart: LocalDate,
        @RequestParam(
            "uploadDateEnd",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) uploadDateEnd: LocalDate,
    ): ResponseEntity<List<MediaEntityUserUploadState>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByUploadDateWithinDateRange(
                uploadDateStart.atStartOfDay(),
                uploadDateEnd.atStartOfDay()
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "Get all greater than paymentDate",
        description = "Gets all greater than paymentDate",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/greaterThanPaymentDate")
    fun getAllGreaterThanPaymentDate(
        @RequestParam(
            "paymentDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDate: LocalDate
    ): ResponseEntity<List<MediaEntityUserUploadState>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByGreaterThanPaymentDate(paymentDate.atStartOfDay()),
            HttpStatus.OK
        )
    }

    @Operation(
        summary = "Get all withing payment date range",
        description = "Gets all greater than paymentDate",
        tags = ["MediaEntitiesUploadState"]
    )
    @GetMapping("/paymentRange")
    fun getAllByPaymentDateRange(
        @RequestParam(
            "paymentDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDateStart: LocalDate,
        @RequestParam(
            "paymentDateEnd",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDateEnd: LocalDate
    ): ResponseEntity<List<MediaEntityUserUploadState>> {
        return ResponseEntity(
            mediaEntityUserUploadStateService.getAllByPaymentDateRange(
                paymentDateStart.atStartOfDay(),
                paymentDateEnd.atStartOfDay()
            ), HttpStatus.OK
        )
    }
}