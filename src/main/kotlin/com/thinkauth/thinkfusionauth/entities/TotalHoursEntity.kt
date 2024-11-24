package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "hour_metrics")
data class TotalHoursEntity(
    var totalDuration:Double? = null,
    var totalCount:Int? = null,
    var acceptedDuration:Double? = null,
    var acceptedCount:Int? = null,
    var rejectedDuration:Double? = null,
    var rejectedCount:Int? = null,
    var pendingDuration:Double? = null,
    var pendingCount:Int? = null
):AuditMetadata()
