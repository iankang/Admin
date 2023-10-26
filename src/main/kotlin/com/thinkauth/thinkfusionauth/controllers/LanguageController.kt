package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.models.responses.LanguageScrapeResponse
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
    fun getLanguages(
    ): ResponseEntity<List<Language>> {
        return ResponseEntity(languageService.getLanguages(),HttpStatus.OK)
    }

    @Operation(
        summary = "Get a language by languageId", description = "gets a language by languageId", tags = ["Language"]
    )
    @GetMapping("/languageById")
    @PreAuthorize("permitAll()")
    fun getLanguageById(
        @RequestParam(name = "languageId") languageId:String,
    ){
        if(languageService.existsByLanguageId(languageId)){
            languageService.getLanguageByLanguageId(languageId)
        }
        throw ResourceNotFoundException("Language with id: $languageId not found")
    }

    @Operation(
        summary = "Get a language by country", description = "gets a language by country", tags = ["Language"]
    )
    @GetMapping("/languageByCountry")
    @PreAuthorize("permitAll()")
    fun getLanguageByCountry(
        @RequestParam(name = "country") country:String,
    ): ResponseEntity<List<Language>> {
        val countryCapitalized = country.capitalize()
        if(languageService.existsByCountry(countryCapitalized)){
           return ResponseEntity(languageService.fetchLanguagesByCountry(countryCapitalized), HttpStatus.OK)
        }
        throw ResourceNotFoundException("Language with country: $country not found")
    }
    @Operation(
        summary = "Get a language by Language Name", description = "gets a language by languageName", tags = ["Language"]
    )
    @GetMapping("/languageByLanguageName")
    @PreAuthorize("permitAll()")
    fun getLanguageByLanguageName(
        @RequestParam(name = "languageName") languageName:String,
    ): ResponseEntity<List<Language?>> {
        val languageReq = LanguageRequest(languageName = languageName)
        if(languageService.existsByLanguageName(languageReq)){
           return ResponseEntity(languageService.findLanguageByLanguageName(languageName = languageName), HttpStatus.OK)
        }
        throw ResourceNotFoundException("Language with country: $languageName not found")
    }


    @Operation(
        summary = "Get languages from wikipedia", description = "gets languages from Wikipedia", tags = ["Language"]
    )
    @GetMapping("/downloadFromWikipedia")
    @PreAuthorize("permitAll()")
    fun downloadFromWikipedia(): MutableList<LanguageScrapeResponse> {
        return languageService.downloadLanguagesFromWikipedia()
    }
    @Operation(
        summary = "Get languages from wikipedia and store in db", description = "gets languages from Wikipedia and stores in db", tags = ["Language"]
    )
    @PostMapping("/downloadAndAddFromWikipedia")
    @PreAuthorize("permitAll()")
    fun downloadAndAddFromWikipedia(): MutableList<Language> {
        val langs = languageService.downloadLanguagesFromWikipedia()

        return languageService.addLanguages(langs)
    }
    @Operation(
        summary = "Delete all languages", description = "deletes all languages", tags = ["Language"]
    )
    @DeleteMapping("/deleteAllLanguages")
    @PreAuthorize("permitAll()")
    fun deleteAllLanguages(){
        languageService.deleteAllLanguages()
    }
}