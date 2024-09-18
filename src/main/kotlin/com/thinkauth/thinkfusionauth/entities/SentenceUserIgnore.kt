package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class SentenceUserIgnore(
    var sentenceId:String,
    var userId:String,
):AuditMetadata()