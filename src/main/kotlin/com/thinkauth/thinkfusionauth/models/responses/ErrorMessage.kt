package com.thinkauth.thinkfusionauth.models.responses

import java.util.Date

data class ErrorMessage(
    val statusCode:Int,
    val timestamp:Date,
    val message:String,
    val description:String
)
