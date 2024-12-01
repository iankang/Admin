package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState

data class MediaEntityLanguageDurationSum(
    var id:MediaEntityLanguageAggregateKey,
    var recordingCount:Int? = null,
    var totalDuration:Float? = null
)
