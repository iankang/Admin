package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class ReviewerPayments(
    var paymentDate:LocalDateTime,

)
