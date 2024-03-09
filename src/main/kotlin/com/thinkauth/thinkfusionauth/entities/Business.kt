package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Business(
    var businessName:String? = null,
    var businessImageProfile:String? = null
) :AuditMetadata(){

}