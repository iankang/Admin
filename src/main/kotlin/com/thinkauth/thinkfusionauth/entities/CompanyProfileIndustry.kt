package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class CompanyProfileIndustry(
    var industryName:String? = null
):AuditMetadata()
