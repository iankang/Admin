package com.thinkauth.thinkfusionauth.models.requests

data class CompanyProfileRequest(
    var companyName:String = "",
    var companyEmail:String = "",
    var companyPhoneNumber:String = "",
    var city:String = "",
    var Address:String = "",
    var websiteLink:String = "",
    var userEmail:String ="",
    var companyProfileIndustryId:String=""
)
