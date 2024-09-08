package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
class SentenceEntity(
    var sentence: String? = null,
    var englishTranslation: String? = null,
    var topic: String? = null,
    var source: String? = null,
    @DocumentReference var language: Language,
    @DocumentReference var business: Business? = null
) : AuditMetadata()

