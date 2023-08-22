package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class Language(
    var languageName:String?= null
):AuditMetadata() {
}