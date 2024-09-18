package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
class SentenceEntitie(
    @TextIndexed
    var sentence: String? = null,
    @TextIndexed
    var englishTranslation: String? = null,
    var topic: String? = null,
    var source: String? = null,
    @Indexed
    @DocumentReference var language: Language,
    @DocumentReference var dialect: Dialect? = null,
    @DocumentReference var business: Business? = null
) : AuditMetadata(){
    var needUploads:Boolean = true
    var acceptedCount:Int? = 0
    var rejectedCount:Int? = 0
}

