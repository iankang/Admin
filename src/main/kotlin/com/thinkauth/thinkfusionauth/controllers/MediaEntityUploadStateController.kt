package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.MediaEntity
import com.thinkauth.thinkfusionauth.entities.MediaEntityUserUploadState
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
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
    private val mediaEntityUserUploadStateService: MediaEntityUserUploadStateService
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
}