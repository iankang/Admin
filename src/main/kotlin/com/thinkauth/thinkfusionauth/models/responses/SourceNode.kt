package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class SourceNode(
    @JsonProperty("content")
    val content: String?,
    @JsonProperty("raw_input")
    val rawInput: RawInput?,
    @JsonProperty("raw_output")
    val rawOutput: RawOutput?,
    @JsonProperty("tool_name")
    val toolName: String?
)