package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.PromptEntity
import com.thinkauth.thinkfusionauth.models.requests.PromptRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.PromptImpl
import com.thinkauth.thinkfusionauth.services.BusinessService
import com.thinkauth.thinkfusionauth.services.DialectService
import com.thinkauth.thinkfusionauth.services.LanguageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/prompts")
@Tag(name = "Prompts", description = "This manages the prompts in the system.")
class PromptController(
    private val promptImpl: PromptImpl,
    private val languageService: LanguageService,
    private val dialectService: DialectService,
    private val businessService: BusinessService
) {
    private val LOGGER: Logger = LoggerFactory.getLogger(PromptController::class.java)

    @Operation(
        summary = "adds a prompt", description = "adds a prompt", tags = ["Prompts"]
    )
    @PostMapping("/addPrompt")
    fun addPrompt(
        @RequestBody promptRequest: PromptRequest
    ): ResponseEntity<PromptEntity> {

        val language = languageService.getLanguageByLanguageId(promptRequest.languageId)
        val dialect = dialectService.getDialectById(promptRequest.dialectId)
        val business = businessService.getSingleBusiness(promptRequest.businessId!!)
        return ResponseEntity.ok(
            promptImpl.createItem(
                PromptEntity(
                    title = promptRequest.title,
                    promptType = promptRequest.promptType,
                    ageRangeEnum = promptRequest.ageRangeEnum,
                    genderState = promptRequest.genderState,
                    url = promptRequest.url,
                    description = promptRequest.description,
                    language = language,
                    dialect = dialect,
                    business = business
                )
            )
        )
    }

    @Operation(
        summary = "gets all prompts", description = "gets all prompts", tags = ["Prompts"]
    )
    @GetMapping("/getPrompts")
    fun getAllPrompts(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<List<PromptEntity>>> {
        LOGGER.debug("getPrompt page: {} size: {}", page, size)
        return ResponseEntity.ok(
            promptImpl.findEverythingPaged(page, size),
        )
    }


}