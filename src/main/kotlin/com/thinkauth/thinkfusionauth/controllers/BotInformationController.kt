package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.entities.Conversation
import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.BotInformationRequest
import com.thinkauth.thinkfusionauth.models.requests.MessageRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.repository.impl.BusinessImpl
import com.thinkauth.thinkfusionauth.services.BotInformationService
import com.thinkauth.thinkfusionauth.services.ConversationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
    private val conversationService: ConversationService,
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

        val botInfo = botInformationService.addBotInformation(botInformationRequest, business)
        val resultBotInfo = botInfoImpl.createItem(botInfo)
        return ResponseEntity(resultBotInfo, HttpStatus.OK)
    }


    @Operation(
        summary = "Get all bots", description = "gets all bots", tags = ["BotInfo"]
    )
    @GetMapping("/getBots")
    fun getBotInfo(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<List<BotInformation>>> {
        return ResponseEntity(botInfoImpl.findEverythingPaged(page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Get a single bot information", description = "gets a single bot's information", tags = ["BotInfo"]
    )
    @GetMapping("/getBotById")
    fun getSingleBot(
        @RequestParam("botId") botId: String
    ): ResponseEntity<BotInformation> {
        if (botInfoImpl.itemExistsById(botId)) {
            return ResponseEntity(botInfoImpl.getItemById(botId), HttpStatus.OK)
        } else {
            throw ResourceNotFoundException("bot with id ${botId} does not exist")
        }
    }

    @Operation(
        summary = "Update a bot by id", description = "Updates a  bot's information", tags = ["BotInfo"]
    )
    @PutMapping("/updateBot/{botId}")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun updateBotById(
        @RequestParam("botId") botId: String,
        @RequestBody botInformation: BotInformation
    ): ResponseEntity<BotInformation> {
        try {
            if(botInfoImpl.itemExistsById(botId)){
                return ResponseEntity(botInfoImpl.updateItem(botId,botInformation),HttpStatus.OK)
            }else {
                throw ResourceNotFoundException("bot with id ${botId} does not exist")
            }
        }catch (e:Exception){
            throw Exception(e.message.toString())
        }
    }

    @Operation(
        summary = "Delete all bot information", description = "Deletes all bot's information", tags = ["BotInfo"]
    )
    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun deleteAllBots(): ResponseEntity<Any> {
        return ResponseEntity(botInfoImpl.deleteAllItems(), HttpStatus.OK)
    }

    @Operation(
        summary = "Delete a single bot information",
        description = "Deletes a single bot's information",
        tags = ["BotInfo"]
    )
    @DeleteMapping("/deleteById")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun deleteABot(
        @RequestParam("botId") botId: String
    ): ResponseEntity<Any> {
        return ResponseEntity(botInfoImpl.deleteItemById(botId), HttpStatus.OK)
    }


}