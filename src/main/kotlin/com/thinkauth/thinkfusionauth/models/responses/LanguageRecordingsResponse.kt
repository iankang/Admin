package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.LanguageMetricsEntity

data class LanguageRecordingsResponse(
    var languageName:String?,
    var languageId:String?,
    var sentenceCount:Long,
    var recordingCount:Long?
){
    fun toLanguageMetricsTbl():LanguageMetricsEntity{
        return LanguageMetricsEntity(
            languageId = languageId,
            languageName = languageName,
            sentenceCount = sentenceCount,
            recordingCount = recordingCount
        )
    }
}
