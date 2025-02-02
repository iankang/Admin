package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class CountyEntity(
    var countyId: Long? = null,
    var name:String? = null,
    var code:Int? = null,
    var capital:String? = null,
):AuditMetadata(){
}