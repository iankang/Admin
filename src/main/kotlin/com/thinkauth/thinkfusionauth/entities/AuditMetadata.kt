package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.annotation.*
import java.time.LocalDateTime

abstract class AuditMetadata(


    @CreatedDate var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate var lastModifiedDate: LocalDateTime = LocalDateTime.now(),

) {
    @Id var id:String? = null
    @CreatedBy var createdByUser: String ? = null
    @LastModifiedBy var modifiedByUser: String? = null
}