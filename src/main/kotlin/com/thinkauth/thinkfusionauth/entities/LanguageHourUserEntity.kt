package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document(collection = "language_hours_user")
data class LanguageHourUserEntity(
    @Indexed
    @DocumentReference
    var userEntity: UserEntity?,
    var email:String? = null,
    var totalDuration:Double? = null,
    var totalCount:Int? = null,
    var acceptedDuration:Double? = null,
    var acceptedCount:Int? = null,
    var rejectedDuration:Double? = null,
    var rejectedCount:Int? = null,
    var pendingDuration:Double? = null,
    var pendingCount:Int? = null
)
