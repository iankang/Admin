package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class LocalLanguage(
    var code:String? = null,
    var languageName:String?= null,
    var country:String? = null,
    var classification:String? = null
):AuditMetadata() {
}