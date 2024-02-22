package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.services.CompanyProfileService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/companyProfile")
@Tag(name = "CompanyProfile", description = "This manages Company Profiles")
class CompanyProfileController(
    private val companyProfileService: CompanyProfileService
) {


}