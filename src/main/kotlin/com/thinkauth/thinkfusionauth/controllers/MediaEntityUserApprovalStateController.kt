package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState
import com.thinkauth.thinkfusionauth.entities.enums.PaymentState
import com.thinkauth.thinkfusionauth.models.requests.RejectionReasonRequest
import com.thinkauth.thinkfusionauth.models.responses.ApproverCount
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityUserApprovalStateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mediaEntitiesApprovalState")
@Tag(name = "MediaEntitiesApprovalState", description = "This manages media entities for approval")
class MediaEntityUserApprovalStateController(
    private val mediaEntityUserApprovalStateService: MediaEntityUserApprovalStateService,
) {

    @Operation(
        summary = "Accept a media entity", description = "accepts a media entity", tags = ["MediaEntitiesApprovalState"]
    )
    @PostMapping("/acceptMediaEntity")
    fun acceptMediaEntity(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String
    ): ResponseEntity<MediaEntityUserApprovalState> {
        return ResponseEntity(mediaEntityUserApprovalStateService.acceptMediaEntity(mediaEntityId), HttpStatus.OK)
    }


    @Operation(
        summary = "Reject a media entity", description = "rejects a media entity", tags = ["MediaEntitiesApprovalState"]
    )
    @PostMapping("/rejectMediaEntity")
    fun rejectMediaEntity(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
        @RequestBody(required = false) rejectionReasonRequest: RejectionReasonRequest?
    ): ResponseEntity<MediaEntityUserApprovalState> {
        return ResponseEntity(mediaEntityUserApprovalStateService.rejectMediaEntity(mediaEntityId,
            rejectionReasonRequest?.rejectionReason
        ), HttpStatus.OK)
    }

    @Operation(
        summary = "Pay an approver", description = "Pays an approver", tags = ["MediaEntitiesApprovalState"]
    )
    @PostMapping("/payApprover")
    fun makePayment(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
        @RequestParam("approverEmail", required = true) approverEmail: String,
    ): ResponseEntity<MediaEntityUserApprovalState> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.makePayment(mediaEntityId, approverEmail), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get media approvals", description = "Gets media approvals", tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/approvalsForMedia")
    fun getApprovalsForSpecificMedia(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("size", required = true) size: Int
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllApprovalsOfSpecificMedia(
                mediaEntityId, page, size
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals by approver email and payment state ",
        description = "Gets approvals by email and payment state",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/approverEmailAndPaymentState")
    fun getByApproverAndPaymentState(
        @RequestParam("approverEmail", required = true) approverEmail: String,
        @RequestParam("paymentState", required = true) paymentState: PaymentState,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("size", required = true) size: Int
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByApproverEmailAndPaymentState(
                approverEmail, paymentState, page, size
            ), HttpStatus.OK
        )
    }
    @Operation(
        summary = "get approvals by approver email and acceptance state ",
        description = "Gets approvals by email and acceptance state",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/approverEmailAndMediaAcceptanceState")
    fun getByApproverAndMediaAcceptanceState(
        @RequestParam("approverEmail", required = true) approverEmail: String,
        @RequestParam("mediaAcceptanceState", required = true) mediaAcceptanceState: MediaAcceptanceState,
        @RequestParam("page", required = true) page: Int? = 0,
        @RequestParam("size", required = true) size: Int? = 100
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByApproverEmailAndMediaAcceptanceState(
                approverEmail, mediaAcceptanceState, page ?: 0, size ?: 100
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals by review date and payment state ",
        description = "Gets approvals by review date and payment state",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/reviewDateAndPaymentState")
    fun getAllByReviewDateAndPaymentState(
        @RequestParam("reviewDate", required = true) reviewDate: LocalDate,
        @RequestParam("paymentState", required = true) paymentState: PaymentState,
        @RequestParam("page", required = true) page: Int? = 0,
        @RequestParam("size", required = true) size: Int? = 100
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByReviewDateAndPaymentState(
                reviewDate.atStartOfDay(), paymentState,page ?: 0, size ?: 100
            ), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals by review date",
        description = "Gets approvals by review date",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/reviewDate")
    fun getAllByReviewDate(
        @RequestParam(
            "reviewDate",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) reviewDate: LocalDate,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("size", required = true) size: Int
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByReviewDate(reviewDate.atStartOfDay(), page, size), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals by review date range",
        description = "Gets approvals by review date range",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/reviewDateRange")
    fun getAllByReviewDateRange(
        @RequestParam(
            "reviewDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) reviewDateStart: LocalDate,
        @RequestParam(
            "reviewDateEnd",
            required = false
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) reviewDateEnd: LocalDate
    ): ResponseEntity<List<MediaEntityUserApprovalState>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByReviewDateRange(reviewDateStart, reviewDateEnd), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals greater than review date start",
        description = "Gets approvals greater than review date start",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/reviewDateStart")
    fun getAllByReviewDateStart(
        @RequestParam(
            "reviewDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) reviewDateStart: LocalDate
    ): ResponseEntity<List<MediaEntityUserApprovalState>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllReviewGreaterThanDate(reviewDateStart), HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals by payment date range",
        description = "Gets approvals by payment date range",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/paymentDateRange")
    fun getAllByPaymentDateRange(
        @RequestParam(
            "reviewDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDateStart: LocalDate,
        @RequestParam(
            "reviewDateEnd",
            required = false
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDateEnd: LocalDate
    ): ResponseEntity<List<MediaEntityUserApprovalState>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByPaymentDateRange(paymentDateStart, paymentDateEnd),
            HttpStatus.OK
        )
    }

    @Operation(
        summary = "get approvals greater than payment date start",
        description = "Gets approvals greater than payment date start",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/paymentDateStart")
    fun getAllByPaymentDateStart(
        @RequestParam(
            "paymentDateStart",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) paymentDateStart: LocalDate
    ): ResponseEntity<List<MediaEntityUserApprovalState>> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getAllByPaymentDateStart(paymentDateStart), HttpStatus.OK
        )
    }
    @Operation(
        summary = "approver metrics",
        description = "Gets approver metrics",
        tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/approverMetrics")
    fun getApproverMetrics(
        @RequestParam(
            "approverEmail",
            required = true
        )  approverEmail: String
    ): ResponseEntity<ApproverCount> {
        return ResponseEntity(
            mediaEntityUserApprovalStateService.getCountByApproverEmail(approverEmail), HttpStatus.OK
        )
    }

}