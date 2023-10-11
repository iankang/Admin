package com.thinkauth.thinkfusionauth.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class ActualBotInput(
    @JsonProperty("user_input")
    var user_input:String? = null
)
