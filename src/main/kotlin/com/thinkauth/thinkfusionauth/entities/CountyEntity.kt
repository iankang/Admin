package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class CountyEntity(
    var countyId: Long = 0L,
    var name:String? = null,
    var code:Int? = null,
    var capital:String? = null,
):AuditMetadata(){
}