package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.enums.EducationLevel
import com.thinkauth.thinkfusionauth.entities.enums.GenderState
import java.time.LocalDate

data class EditUserRequest(
    var email: String? = null,
    var firstName: String? = null,
    var fullName: String? = null,
    var imageUrl: String? = null,
    var lastName: String? = null,
    var middleName: String? = null,
    var mobilePhone: String? = null,
    var birthDate: LocalDate? = null,
    var ageRangeEnum: AgeRangeEnum?,
    var gender: GenderState?,
    var educationLevel:EducationLevel?,
    var dialectId:String? = null,
    var nationalId:Long? = null
)
