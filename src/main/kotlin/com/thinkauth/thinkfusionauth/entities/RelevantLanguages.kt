package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
class RelevantLanguages(
    @Indexed
    var code:String? = null,
    @Indexed
    var languageName:String?= null,
    @Indexed
    var languageId:String?= null,
    var country:String? = null,
    var classification:String? = null
):AuditMetadata() {
}