package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class ConstituencyEntity(
    var wardName:String?,
    var countyId:Int?,
    var countyName:String?,
    var constituencyName:String?
):AuditMetadata()
