package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.CompanyProfileIndustry
import com.thinkauth.thinkfusionauth.entities.Language
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileIndustryRequest
import com.thinkauth.thinkfusionauth.models.requests.LanguageRequest
import com.thinkauth.thinkfusionauth.services.CompanyProfileIndustryService
import com.thinkauth.thinkfusionauth.services.CompanyProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/companyProfileIndustry")
@Tag(name = "CompanyProfileIndustry", description = "This manages Company Profile Industry")
class CompanyProfileIndustryController(
    private val companyProfileIndustryService: CompanyProfileIndustryService
) {

    @Operation(
        summary = "Add an industry", description = "adds an industry", tags = ["CompanyProfileIndustry"]
    )
    @PostMapping("/addCompanyProfileIndustry")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addIndustry(
        @RequestBody companyProfileIndustryRequest: CompanyProfileIndustryRequest
    ): ResponseEntity<CompanyProfileIndustry> {
        if(!companyProfileIndustryService.existByIndustryName(companyProfileIndustryRequest.industryName)){

            return ResponseEntity(companyProfileIndustryService.addCompanyProfileIndustry(companyProfileIndustryRequest), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @Operation(
        summary = "Get all industries", description = "gets all industries", tags = ["CompanyProfileIndustry"]
    )
    @GetMapping("/allCompanyIndustries")
    fun getAllIndustries(): ResponseEntity<MutableList<CompanyProfileIndustry>> {
        return ResponseEntity(companyProfileIndustryService.fetchAllCompanyProfileIndustries(),HttpStatus.OK)
    }

    @Operation(
        summary = "Get an industry", description = "gets an industry", tags = ["CompanyProfileIndustry"]
    )
    @GetMapping("/aCompanyIndustry/{industryId}")
    fun getIndustryById(
        @PathVariable("industryId") industryId:String
    ): ResponseEntity<CompanyProfileIndustry> {
        return ResponseEntity(companyProfileIndustryService.fetchCompanyProfileIndustryById(industryId),HttpStatus.OK)
    }

    @Operation(
        summary = "update an industry", description = "updates an industry", tags = ["CompanyProfileIndustry"]
    )
    @PutMapping("/companyIndustry/{industryId}")
    fun updateIndustry(
        @PathVariable("industryId") industryId:String,
        @RequestBody industryRequest: CompanyProfileIndustryRequest
    ): ResponseEntity<CompanyProfileIndustry> {
        return ResponseEntity(companyProfileIndustryService.updateCompanyProfileIndustry(industryId,industryRequest), HttpStatus.OK)
    }

    @Operation(
        summary = "delete an industry", description = "deletes an industry", tags = ["CompanyProfileIndustry"]
    )
    @DeleteMapping("/companyIndustry/{industryId}")
    fun deleteIndustry(
        @PathVariable("industryId") industryId:String,
    ): ResponseEntity<Unit> {
        return ResponseEntity(companyProfileIndustryService.deleteCompanyProfileIndustry(industryId),HttpStatus.OK)
    }
}