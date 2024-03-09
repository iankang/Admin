package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.BotInformationRequest
import com.thinkauth.thinkfusionauth.models.requests.BusinessRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.repository.impl.BusinessImpl
import com.thinkauth.thinkfusionauth.services.BotInformationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/botInfo")
@Tag(name = "BotInfo", description = "Bot Information operations")
class BotInformationController(
    private val botInformationService: BotInformationService,
    private val botInfoImpl: BotInfoImpl,
    private val businessImpl: BusinessImpl
) {

    @Operation(
        summary = "Add bot information", description = "adds bot information", tags = ["BotInfo"]
    )
    @PostMapping("/addBotInfo")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addBotInfo(
        @RequestBody botInformationRequest: BotInformationRequest
    ): ResponseEntity<BotInformation> {

        val business: Business?
        if (botInformationRequest.businessId != null) {
            business = businessImpl.getItemById(botInformationRequest.businessId!!)
        } else {
            throw ResourceNotFoundException("business with id: ${botInformationRequest.businessId} is not available")
        }

        val botInfo = botInformationService.addBotInformation(botInformationRequest,business)
        val resultBotInfo = botInfoImpl.createItem(botInfo)
        return ResponseEntity(resultBotInfo,HttpStatus.OK)
    }

    @Operation(
        summary = "Get all bots", description = "gets all bots", tags = ["BotInfo"]
    )
    @GetMapping("/getBots")
    fun getBotInfo(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<BotInformation>>> {
        return ResponseEntity(botInfoImpl.findEverythingPaged(page, size),HttpStatus.OK)
    }
}