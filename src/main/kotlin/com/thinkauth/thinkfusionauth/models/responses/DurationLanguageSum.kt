package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState

data class DurationLanguageSum(
    var id:AggregateKey,
    var recordingCount:Int? = null,
    var totalDuration:Float? = null
)
