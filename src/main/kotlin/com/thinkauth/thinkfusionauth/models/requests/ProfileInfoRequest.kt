package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.GenderState

data class ProfileInfoRequest(
    var ageRangeEnum: AgeRangeEnum?,
    var gender:GenderState?,
    var dialectId:String?
)
