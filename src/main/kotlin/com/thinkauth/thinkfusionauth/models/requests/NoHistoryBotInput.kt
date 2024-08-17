package com.thinkauth.thinkfusionauth.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class NoHistoryBotInput(
    @JsonProperty("sender")
    var user_info:String? = null,
    @JsonProperty("message")
    var message:String? = null
)
