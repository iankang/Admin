package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.CompanyProfileIndustry
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.CompanyProfileIndustryRequest
import com.thinkauth.thinkfusionauth.repository.CompanyProfileIndustryRepository
import org.springframework.stereotype.Service

@Service
class CompanyProfileIndustryService(
    private val companyProfileIndustryRepository: CompanyProfileIndustryRepository
) {

    fun addAllIndustries(companyProfileIndustry: List<CompanyProfileIndustry>): MutableList<CompanyProfileIndustry> {
       return  companyProfileIndustryRepository.saveAll(companyProfileIndustry)
    }

    fun addCompanyProfileIndustry(companyProfileIndustryRequest: CompanyProfileIndustryRequest): CompanyProfileIndustry {
        val companyProfileIndustry = CompanyProfileIndustry(companyProfileIndustryRequest.industryName)
        return companyProfileIndustryRepository.save(companyProfileIndustry)
    }

    fun addCompanyProfileIndustry(companyProfileIndustry: CompanyProfileIndustry): CompanyProfileIndustry {
        return companyProfileIndustryRepository.save(companyProfileIndustry)
    }

    fun fetchAllCompanyProfileIndustries(): MutableList<CompanyProfileIndustry> {
        return companyProfileIndustryRepository.findAll()
    }

    fun fetchCompanyProfileIndustryById(companyProfileId:String): CompanyProfileIndustry? {
        return companyProfileIndustryRepository.findById(companyProfileId).orElseThrow { ResourceNotFoundException("item with id: ${companyProfileId} is missing") }
    }

    fun updateCompanyProfileIndustry(industryId:String, companyProfileIndustryRequest: CompanyProfileIndustryRequest): CompanyProfileIndustry {
        val companyProfileIndustry = fetchCompanyProfileIndustryById(industryId)
        companyProfileIndustry?.industryName = companyProfileIndustryRequest?.industryName

        return addCompanyProfileIndustry(companyProfileIndustry!!)
    }

    fun deleteCompanyProfileIndustry(industryId:String){
        companyProfileIndustryRepository.deleteById(industryId)
    }
    fun fetchIndustryCount(): Long {
        return companyProfileIndustryRepository.count()
    }

    fun existByIndustryName(industryName:String): Boolean {
        return companyProfileIndustryRepository.existsByIndustryName(industryName)
    }
}