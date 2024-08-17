package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
class BotInformation(
    var botName:String? = null,
    var botDescription:String? = null,
    var botLogoUrl:String? = null,
    var botUrl:String? = null,
    var botPort:Int? = null,
    var botPath:String? = null,
    var botIsAvailable:Boolean? = false,
    @DBRef
    var business: Business? = null,
    var botType:BotTypeEnum
): AuditMetadata(){
}