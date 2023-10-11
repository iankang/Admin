package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
    @JsonProperty("content")
    val content: String?,
    @JsonProperty("raw_input")
    val rawInput: RawInputX?,
    @JsonProperty("raw_output")
    val rawOutput: RawOutput?,
    @JsonProperty("tool_name")
    val toolName: String?
)