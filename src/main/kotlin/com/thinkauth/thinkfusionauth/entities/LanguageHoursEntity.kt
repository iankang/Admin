package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "language_hours")
data class LanguageHoursEntity(
    @Indexed
    var languageName:String?,
    @Indexed
    var languageId:String?,
    var totalDuration:Double? = null,
    var totalCount:Int? = null,
    var acceptedDuration:Double? = null,
    var acceptedCount:Int? = null,
    var rejectedDuration:Double? = null,
    var rejectedCount:Int? = null,
    var pendingDuration:Double? = null,
    var pendingCount:Int? = null
)
