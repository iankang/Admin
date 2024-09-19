package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.DialectRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.services.DialectService
import com.thinkauth.thinkfusionauth.utils.toStandardCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/dialect")
@Tag(name = "Dialect", description = "This manages Audio Language Dialects")
class DialectRestController(
    private val dialectService: DialectService,
) {
    private val logger: Logger = LoggerFactory.getLogger(DialectRestController::class.java)

    @Operation(
        summary = "Add a dialect", description = "adds a dialect", tags = ["Dialect"]
    )
    @PostMapping("/addDialect")
    @PreAuthorize("permitAll()")
    fun addDialect(
        @RequestBody dialectRequest: DialectRequest
    ): ResponseEntity<Dialect> {
        logger.info("dialectRequest: $dialectRequest")
        if (!dialectService.existsByDialectName(dialectRequest.dialectName!!)) {
            return ResponseEntity(dialectService.addDialect(dialectRequest), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @Operation(
        summary = "Get all dialects", description = "gets all dialects", tags = ["Dialect"]
    )
    @GetMapping("/dialects")
    @PreAuthorize("permitAll()")
    fun getDialects(
        @RequestParam("page", defaultValue = "0") page: Int = 0,
        @RequestParam("size", defaultValue = "10") size: Int = 10
    ): ResponseEntity<PagedResponse<List<Dialect>>> {
        return ResponseEntity(dialectService.getDialects(page, size), HttpStatus.OK)
    }

    @Operation(
        summary = "Get a dialect by dialectId", description = "gets a dialect by dialectId", tags = ["Dialect"]
    )
    @GetMapping("/dialectById")
    @PreAuthorize("permitAll()")
    fun getDialectById(
        @RequestParam(name = "dialectId") dialectId: String,
    ): ResponseEntity<Dialect> {
        if (dialectService.existsByDialectId(dialectId)) {
            return ResponseEntity(dialectService.getDialectById(dialectId), HttpStatus.OK)
        }
        throw ResourceNotFoundException("Dialect with id: $dialectId not found")
    }

    @Operation(
        summary = "Get a dialect by languageName", description = "gets a dialect by languageName", tags = ["Dialect"]
    )
    @GetMapping("/dialectByLanguageName")
    @PreAuthorize("permitAll()")
    fun getDialectByLanguageName(
        @RequestParam(name = "languageName") languageName: String,
    ): ResponseEntity<List<Dialect>> {

        val dialectCount = dialectService.getDialectCountByLanguageName(languageName.toStandardCase())
        logger.info("count of dialects: $dialectCount")
        if (dialectCount > 0L) {
            return ResponseEntity(dialectService.getDialectByLanguageName(languageName.toStandardCase()), HttpStatus.OK)
        }
        throw ResourceNotFoundException("Dialect with language name: $languageName not found")
    }

    @Operation(
        summary = "Get a dialect by dialectName", description = "gets a dialect by dialectName", tags = ["Dialect"]
    )
    @GetMapping("/dialectByDialectName")
    @PreAuthorize("permitAll()")
    fun getDialectByDialectName(
        @RequestParam(name = "dialectName") dialectName: String,
    ): ResponseEntity<List<Dialect>> {
        return ResponseEntity(dialectService.getDialectByDialectName(dialectName), HttpStatus.OK)
    }

    @Operation(
        summary = "Updates a dialect", description = "Updates a dialect", tags = ["Dialect"]
    )
    @PutMapping("/updateDialect")
    @PreAuthorize("permitAll()")
    fun updateDialect(
        @RequestParam(name = "dialectId") dialectId: String, @RequestBody dialect: Dialect
    ) {
        dialectService.updateDialect(dialectId, dialect)
    }

    @Operation(
        summary = "Deletes a dialect", description = "deletes a dialect", tags = ["Dialect"]
    )
    @DeleteMapping("/deleteADialect")
    @PreAuthorize("permitAll()")
    fun deleteADialect(
        @RequestParam(name = "dialectId") dialectId: String,
    ) {
        dialectService.deleteDialectById(dialectId)
    }

    @Operation(
        summary = "Delete all dialects", description = "deletes all dialects", tags = ["Dialect"]
    )
    @DeleteMapping("/deleteAllDialects")
    @PreAuthorize("permitAll()")
    fun deleteAllLanguages() {
        dialectService.deleteAllDialects()
    }
}