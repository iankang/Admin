package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.enums.GenderState

data class ProfileInfoRequest(
    var ageRangeEnum: AgeRangeEnum?,
    var gender: GenderState?,
    var dialectId:String?
)
