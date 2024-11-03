package com.thinkauth.thinkfusionauth.models.responses

data class ApproverCount(
    var mediaEntitiesCount:Long,
    var acceptedCount:Long,
    var rejectedCount:Long,
    var paidCount:Long,
    var unpaidCount:Long,
    var pendingCount:Long,
)
