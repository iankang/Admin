package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.entities.LocalLanguage
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.requests.LocalLanguageRequest
import com.thinkauth.thinkfusionauth.models.requests.SampleWords
import com.thinkauth.thinkfusionauth.models.responses.LanguageScrapeResponse
import com.thinkauth.thinkfusionauth.repository.impl.LocalLanguageImpl
import com.thinkauth.thinkfusionauth.services.LanguageService
import com.thinkauth.thinkfusionauth.services.ScrapingService
import com.thinkauth.thinkfusionauth.utils.toStandardCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.bouncycastle.asn1.x500.style.RFC4519Style.l
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/LocalLanguage")
@Tag(name = "LocalLanguage", description = "This manages Local Languages")
class LocalLanguageController(
    private val languageService: LocalLanguageImpl,
    private val scrapingService: ScrapingService
) {
    @Operation(
        summary = "Add a language", description = "adds a language", tags = ["Language"]
    )
    @PostMapping("/addLocalLanguage")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addLanguage(
        @RequestBody languageRequest: LocalLanguageRequest
    ): ResponseEntity<LocalLanguage> {
        if(!languageService.existsByLanguageName(languageRequest.languageName?.toStandardCase() ?: "")){

            val localLanguage = LocalLanguage(
                code = languageRequest.code,
                languageName = languageRequest.languageName,
                country = languageRequest.country,
                classification = languageRequest.classification
            )
            return ResponseEntity(languageService.createItem(localLanguage), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @Operation(
        summary = "Get all languages", description = "gets all languages", tags = ["Language"]
    )
    @GetMapping("/localLanguages")
    @PreAuthorize("permitAll()")
    fun getLanguages(
    ): ResponseEntity<MutableList<LocalLanguage>> {
        return ResponseEntity(languageService.getAllLanguages(),HttpStatus.OK)
    }

    @Operation(
        summary = "Get a language by languageId", description = "gets a language by languageId", tags = ["Language"]
    )
    @GetMapping("/localLanguageById")
    @PreAuthorize("permitAll()")
    fun getLanguageById(
        @RequestParam(name = "languageId") languageId:String,
    ){
        if(languageService.itemExistsById(languageId)){
            languageService.getItemById(languageId)
        }
        throw ResourceNotFoundException("Language with id: $languageId not found")
    }

    @Operation(
        summary = "Get a language by Language Name", description = "gets a language by languageName", tags = ["Language"]
    )
    @GetMapping("/localLanguageByLanguageName")
    @PreAuthorize("permitAll()")
    fun getLanguageByLanguageName(
        @RequestParam(name = "languageName") languageName:String,
    ): ResponseEntity<List<LocalLanguage>> {
        if(languageService.existsByLanguageName(languageName.toStandardCase())){
           return ResponseEntity(languageService.getLanguageByLanguageName(languageName = languageName.toStandardCase()), HttpStatus.OK)
        }
        throw ResourceNotFoundException("Language with name: $languageName not found")
    }



}