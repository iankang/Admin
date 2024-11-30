package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
data class Dialect(
    var dialectName:String? = null,
    var language: Language? = null
):AuditMetadata()