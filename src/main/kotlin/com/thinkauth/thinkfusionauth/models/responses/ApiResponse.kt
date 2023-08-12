package com.thinkauth.thinkfusionauth.models.responses

import com.inversoft.error.Errors

data class FusionApiResponse<T>(
    val status:Int? = 0,
    val message:T? = null,
    val error:Errors? = null,
)
