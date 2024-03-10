package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class BotInformation(
    var botName:String? = null,
    var botLogoUrl:String? = null,
    var botUrl:String? = null,
    var botPort:Int? = null,
    var botPath:String? = null,
    var botIsAvailable:Boolean? = false,
    var business: Business? = null
): AuditMetadata(){
}