package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.enums.EducationLevel
import com.thinkauth.thinkfusionauth.entities.enums.GenderState

data class ProfileInfoRequest(
    var ageRangeEnum: AgeRangeEnum?,
    var gender: GenderState?,
    var educationLevel: EducationLevel?,
    var dialectId:String?,
    var nationalId:String?
)
