package com.thinkauth.thinkfusionauth.models.requests

import java.time.LocalDate

data class BotInput(
   var sender:String? = null,
   var message:String? = null,
   var createdAt:LocalDate = LocalDate.now()
)
