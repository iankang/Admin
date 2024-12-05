package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.enums.EducationLevel
import com.thinkauth.thinkfusionauth.entities.enums.EmploymentState
import com.thinkauth.thinkfusionauth.entities.enums.GenderState

data class UserData(
    var constituencyId:String? = null,
    var constituencyName:String? = null,
    var countyId:Int? = 0,
    var countyName:String? = null,
    var dialectId:String? = null,
    var educationLevel: String? = null,
    var employmentState: String? = null,
    var genderState: String? = null,
    var languageId:String? = null,
    var nationalId:Long? = null
)
