package com.thinkauth.thinkfusionauth.entities

import org.checkerframework.common.aliasing.qual.Unique
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "language_metrics")
data class LanguageMetricsEntity(
    @Indexed
    var languageName:String?,
    @Indexed
    var languageId:String?,
    var sentenceCount:Long,
    var recordingCount:Long
)
