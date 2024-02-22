package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.CompanyProfile
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileRequest
import com.thinkauth.thinkfusionauth.services.CompanyProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/companyProfile")
@Tag(name = "CompanyProfile", description = "This manages Company Profiles")
class CompanyProfileController(
    private val companyProfileService: CompanyProfileService
) {

    @Operation(
        summary = "Add a company Profile", description = "adds a company profile", tags = ["CompanyProfile"]
    )
    @PostMapping("/addCompanyProfile")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addCompanyProfile(
        @RequestBody companyProfileRequest: CompanyProfileRequest
    ): ResponseEntity<CompanyProfile> {
        return ResponseEntity(companyProfileService.addCompanyProfile(companyProfileRequest),HttpStatus.OK)
    }

    @Operation(
        summary = "Get all company profiles", description = "gets all company profiles", tags = ["CompanyProfile"]
    )
    @GetMapping("/allCompanyProfiles")
    fun getAllCompanyProfiles(): ResponseEntity<MutableList<CompanyProfile>> {
        return ResponseEntity(companyProfileService.fetchAllCompanyProfiles(),HttpStatus.OK)
    }

    @Operation(
        summary = "Get all company profiles", description = "gets all company profiles", tags = ["CompanyProfile"]
    )
    @GetMapping("/companyProfile/{profileId}")
    fun getCompanyProfileById(
        @PathVariable("profileId") profileId:String
    ): ResponseEntity<CompanyProfile> {
        return ResponseEntity(companyProfileService.fetchCompanyProfileByProfileId(profileId),HttpStatus.OK)
    }
    @Operation(
        summary = "Get all company profiles by industry name", description = "gets all company profiles by industry name", tags = ["CompanyProfile"]
    )
    @GetMapping("/companyProfilesByIndustryname/{industryName}")
    fun getCompanyProfileByIndustryName(
       @PathVariable("industryName") industryName:String
    ): ResponseEntity<List<CompanyProfile>> {
        return ResponseEntity(companyProfileService.findCompanyProfilesByIndustryName(industryName),HttpStatus.OK)
    }

    @Operation(
        summary = "Get all company profiles by email", description = "gets all company profiles by email", tags = ["CompanyProfile"]
    )
    @GetMapping("/companyProfilesByUser/{email}")
    fun getCompanyProfileByEmail(
       @PathVariable("email") email:String
    ): ResponseEntity<List<CompanyProfile>> {
        return ResponseEntity(companyProfileService.findCompanyProfilesByUser(email),HttpStatus.OK)
    }

}