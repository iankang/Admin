package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.BotTypeEnum
import com.thinkauth.thinkfusionauth.entities.Business
import org.springframework.web.multipart.MultipartFile

data class BotInformationRequest(
    var botName:String? = null,
    var botDescription:String? = null,
    var botLogoUrl:String? = null,
    var botUrl:String? = null,
    var botPath:String? = null,
    var botPort:Int? = null,
    var businessId: String? = null,
    var botType: BotTypeEnum
)
