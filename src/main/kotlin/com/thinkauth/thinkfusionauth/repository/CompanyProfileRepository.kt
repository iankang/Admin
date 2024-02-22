package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.CompanyProfile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyProfileRepository :MongoRepository<CompanyProfile,String> {

    fun findAllByIndustryIndustryName(industryName:String): List<CompanyProfile>

    fun findAllByUserEntityEmail(email:String):List<CompanyProfile>

}