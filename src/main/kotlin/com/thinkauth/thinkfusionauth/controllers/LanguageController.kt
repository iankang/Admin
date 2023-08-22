package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.services.LanguageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/language")
@Tag(name = "Language", description = "This manages Audio Languages")
class LanguageController(
    private val languageService: LanguageService
) {
    @Operation(
        summary = "Add a language", description = "adds a language", tags = ["Language"]
    )
    @PostMapping("/addLanguage")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addLanguage(languageRequest: LanguageRequest): ResponseEntity<Language> {
        if(!languageService.existsByLanguageName(languageRequest)){

            return ResponseEntity(languageService.addLanguage(languageRequest), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @Operation(
        summary = "Get all languages", description = "gets all languages", tags = ["Language"]
    )
    @GetMapping("/languages")
    @PreAuthorize("permitAll()")
    fun getBusinesses(
    ): ResponseEntity<List<Language>> {
        return ResponseEntity(languageService.getLanguages(),HttpStatus.OK)
    }
}