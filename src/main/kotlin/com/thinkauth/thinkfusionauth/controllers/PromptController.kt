package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.models.requests.PromptRequest
import com.thinkauth.thinkfusionauth.repository.impl.PromptImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/prompts")
@Tag(name = "Prompts", description = "This manages the prompts in the system.")
class PromptController(
    private val promptImpl: PromptImpl
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(PromptController::class.java)

    @Operation(
        summary = "adds a prompt", description = "adds a prompt", tags = ["Prompts"]
    )
    @PostMapping("/addPrompt")
    fun addPrompt(
        @RequestBody promptRequest: PromptRequest
    ){

    }
}