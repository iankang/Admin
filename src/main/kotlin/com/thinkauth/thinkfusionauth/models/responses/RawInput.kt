package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class RawInput(
    @JsonProperty("args")
    val args: List<Any?>?,
    @JsonProperty("kwargs")
    val kwargs: Kwargs?
)