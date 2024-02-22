package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.CompanyProfileIndustry
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyProfileIndustryRepository: MongoRepository<CompanyProfileIndustry,String> {

    fun existsByIndustryName(industryName:String):Boolean
}