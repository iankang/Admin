// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.thinkauth.thinkfusionauth.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

class CountyResponse (
    @JsonProperty("constituencies")
    var constituencies: List<Constituency?>?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("No")
    var no: Int?
)

class Constituency (
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("wards")
    var wards: List<String?>?
)
