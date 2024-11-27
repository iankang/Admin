package com.thinkauth.thinkfusionauth.models.responses

import com.thinkauth.thinkfusionauth.entities.enums.MediaAcceptanceState

data class AggregateKey(
    val languageId: String?,
    val languageName: String?,
    val mediaState: MediaAcceptanceState?
)