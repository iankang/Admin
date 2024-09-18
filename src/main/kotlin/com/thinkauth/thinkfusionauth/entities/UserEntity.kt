package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import org.checkerframework.common.aliasing.qual.Unique
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class UserEntity(
    var username:String?= null,
    var birthDate:LocalDate?= null,
    var email:String?= null,
    var firstName:String?= null,
    var middleName:String?= null,
    var lastName:String?= null,
    var imageUrl:String?= null,
    var mobilePhone:String?= null,
    var languageId:String? = null,
    var genderState: String? = null,
    var ageGroup:String? =  null,
    var nationalId:Long? = null
):AuditMetadata()
