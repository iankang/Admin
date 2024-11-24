package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Language(
    @Indexed
    var code:String? = null,
    @Indexed
    var languageName:String?= null,
    var country:String? = null,
    var classification:String? = null
):AuditMetadata() {

    fun toRelevantLanguageTbl():RelevantLanguages{
        return RelevantLanguages(
            code = code,
            languageName = languageName,
            country = country,
            classification= classification
        )
    }
}