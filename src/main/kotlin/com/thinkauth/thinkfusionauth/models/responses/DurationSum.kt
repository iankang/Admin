package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState

data class DurationSum(
    var id: MediaAcceptanceState? = null,
    var stateCount:Int? = null,
    var totalDuration:Float? = null
)
