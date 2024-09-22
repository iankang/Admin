package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntityUserApprovalState
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.MediaEntityUserApprovalStateService
import com.thinkauth.thinkfusionauth.services.MediaEntityUserUploadStateService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String
    ): ResponseEntity<MediaEntityUserApprovalState> {
       return ResponseEntity(mediaEntityUserApprovalStateService.rejectMediaEntity(mediaEntityId), HttpStatus.OK)
    }

    @Operation(
        summary = "Pay an approver", description = "Pays an approver", tags = ["MediaEntitiesApprovalState"]
    )
    @PostMapping("/payApprover")
    fun makePayment(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
        @RequestParam("approverEmail", required = true) approverEmail: String,
    ): ResponseEntity<MediaEntityUserApprovalState> {
        return ResponseEntity(mediaEntityUserApprovalStateService.makePayment(mediaEntityId, approverEmail), HttpStatus.OK)
    }

    @Operation(
        summary = "get media approvals", description = "Gets media approvals", tags = ["MediaEntitiesApprovalState"]
    )
    @GetMapping("/approvalsForMedia")
    fun getApprovalsForSpecificMedia(
        @RequestParam("mediaEntityId", required = true) mediaEntityId: String,
        @RequestParam("page", required = true)page: Int,
        @RequestParam("size", required = true)size: Int
    ): ResponseEntity<PagedResponse<MutableList<MediaEntityUserApprovalState>>> {
        return ResponseEntity(mediaEntityUserApprovalStateService.getAllApprovalsOfSpecificMedia(mediaEntityId, page, size), HttpStatus.OK)
    }
}