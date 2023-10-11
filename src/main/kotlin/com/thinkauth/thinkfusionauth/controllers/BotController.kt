package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.models.requests.ActualBotInput
import com.thinkauth.thinkfusionauth.models.requests.BotInput
import com.thinkauth.thinkfusionauth.models.responses.GeneralBotResponse
import com.thinkauth.thinkfusionauth.services.BotService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/bot")
@Tag(name = "Bot", description = "This interacts with the bot")
class BotController(
    @Value("\${app.bot-url}")
    private val botUrl: String? = null,
    private val botService: BotService
) {

    private val logger: Logger = LoggerFactory.getLogger(BotController::class.java)


    @Operation(
        summary = "post to bot", description = "posts to bot", tags = ["Bot"]
    )
    @PostMapping("/interact")
    fun interact(
        @RequestBody input: BotInput
    ): ResponseEntity<GeneralBotResponse>? {
        try {
            val actualBot = ActualBotInput(input.message!!)
            return botService.interactWithBot("$botUrl", actualBot)
        }catch(e:Exception){
            logger.error("error: ${e.message}")
        }
        return null
    }
}