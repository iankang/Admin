package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class RawOutput(
    @JsonProperty("body")
    val body: String?,
    @JsonProperty("href")
    val href: String?,
    @JsonProperty("title")
    val title: String?
)