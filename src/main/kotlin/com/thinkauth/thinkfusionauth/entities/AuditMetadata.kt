package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.annotation.*
import java.time.LocalDateTime

abstract class AuditMetadata(
    @Id var id:String? = null,

    @CreatedDate var createdDate: LocalDateTime? = null,

    @LastModifiedDate var lastModifiedDate: LocalDateTime? = null,

    @CreatedBy var createdByUser: String? = null,

    @LastModifiedBy var modifiedByUser: String? = null
) {

}