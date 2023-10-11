package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class Kwargs(
    @JsonProperty("keywords")
    val keywords: String?
)