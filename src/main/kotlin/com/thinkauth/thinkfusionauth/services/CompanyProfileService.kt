package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.CompanyProfile
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileRequest
import com.thinkauth.thinkfusionauth.repository.CompanyProfileRepository
import org.springframework.stereotype.Service

@Service
class CompanyProfileService(
    private val companyProfileIndustryService: CompanyProfileIndustryService,
    private val companyProfileRepository: CompanyProfileRepository,
    private val userManagementService: UserManagementService
) {

    fun addCompanyProfile(
        companyProfileRequest: CompanyProfileRequest
    ): CompanyProfile {
        val industry = companyProfileIndustryService.fetchCompanyProfileIndustryById(companyProfileRequest.companyProfileIndustryId)
        val user = userManagementService.fetchUserEntityByEmail(companyProfileRequest.userEmail)
        val companyProfile = CompanyProfile(
            companyName = companyProfileRequest.companyName,
            companyEmail = companyProfileRequest.companyEmail,
            companyPhoneNumber = companyProfileRequest.companyPhoneNumber,
            city = companyProfileRequest.city,
            Address = companyProfileRequest.Address,
            websiteLink = companyProfileRequest.websiteLink,
            industry = industry!!,
            userEntity = user
        )
        return companyProfileRepository.save(companyProfile)
    }

    fun addCompanyProfile(companyProfileEntity:CompanyProfile): CompanyProfile {
        return companyProfileRepository.save(companyProfileEntity)
    }

    fun fetchCompanyProfileByProfileId(
        companyProfileId:String
    ): CompanyProfile? {
        return companyProfileRepository.findById(companyProfileId).orElseThrow { ResourceNotFoundException("profile with id: ${companyProfileId} not found") }
    }

    fun fetchAllCompanyProfiles(): MutableList<CompanyProfile> {
        return companyProfileRepository.findAll()
    }

    fun updateCompanyProfile(
        companyProfileId:String,
        companyProfileRequest: CompanyProfileRequest,
        email:String,
        industryId:String
    ): CompanyProfile {
        val companyProfileUpdate = fetchCompanyProfileByProfileId(companyProfileId)
        val industry = companyProfileIndustryService.fetchCompanyProfileIndustryById(industryId)
        val user = userManagementService.fetchUserEntityByEmail(email)

       companyProfileUpdate?.companyName = companyProfileRequest.companyName
        companyProfileUpdate?.companyPhoneNumber = companyProfileRequest.companyPhoneNumber
        companyProfileUpdate?.companyEmail = companyProfileRequest.companyEmail
        companyProfileUpdate?.city = companyProfileRequest.city
        companyProfileUpdate?.Address = companyProfileRequest.Address
        companyProfileUpdate?.websiteLink = companyProfileRequest.websiteLink
        companyProfileUpdate?.userEntity = user
        companyProfileUpdate?.industry = industry!!

        return addCompanyProfile(companyProfileUpdate!!)

    }

    fun findCompanyProfilesByIndustryName(industryName:String): List<CompanyProfile> {
        return  companyProfileRepository.findAllByIndustryIndustryName(industryName)
    }

    fun findCompanyProfilesByUser(userEmail:String): List<CompanyProfile> {
        return companyProfileRepository.findAllByUserEntityEmail(userEmail)
    }
}