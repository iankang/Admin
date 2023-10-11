package com.thinkauth.thinkfusionauth.models.responses


import com.fasterxml.jackson.annotation.JsonProperty

data class GeneralBotResponse(
    @JsonProperty("response")
    val response: String?,
    @JsonProperty("source_nodes")
    val sourceNodes: List<SourceNode>?,
    @JsonProperty("sources")
    val sources: List<Source>?
)