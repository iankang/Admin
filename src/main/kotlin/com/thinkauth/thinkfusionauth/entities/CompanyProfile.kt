package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class CompanyProfile(
    var companyName:String = "",
    var companyEmail:String = "",
    var companyPhoneNumber:String = "",
    var city:String = "",
    var Address:String = "",
    var websiteLink:String = "",
    var industry: CompanyProfileIndustry,
    var userEntity: UserEntity
)
