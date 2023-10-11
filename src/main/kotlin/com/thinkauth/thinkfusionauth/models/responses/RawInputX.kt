package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class RawInputX(
    @JsonProperty("args")
    val args: List<Any>?,
    @JsonProperty("kwargs")
    val kwargs: String ?
)