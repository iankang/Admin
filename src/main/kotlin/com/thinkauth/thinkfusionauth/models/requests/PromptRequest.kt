package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.enums.GenderState
import com.thinkauth.thinkfusionauth.entities.enums.PromptType

data class PromptRequest(
    var title:String,
    var promptType: PromptType?,
    var languageId:String,
    var dialectId:String,
    var ageRangeEnum: AgeRangeEnum? = null,
    var genderState: GenderState? = null,
    var url:String? = null,
    var description:String? = null,
    var businessId:String? = null
)
