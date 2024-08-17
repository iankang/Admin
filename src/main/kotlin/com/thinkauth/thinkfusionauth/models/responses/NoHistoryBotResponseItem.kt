package com.thinkauth.thinkfusionauth.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class NoHistoryBotResponseItem(
    @JsonProperty("recipient_id")
    val recepient: String?,
    @JsonProperty("text")
    val message: String?,
)
