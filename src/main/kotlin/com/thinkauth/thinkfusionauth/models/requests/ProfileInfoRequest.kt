package com.thinkauth.thinkfusionauth.models.requests

import com.thinkauth.thinkfusionauth.entities.GenderState

data class ProfileInfoRequest(
    var gender:GenderState?,
    var dialectId:String?
)
