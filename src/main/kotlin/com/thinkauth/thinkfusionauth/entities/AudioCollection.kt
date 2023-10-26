package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
class AudioCollection(
    var sentence:String? = null,
    var audio: String? = null,
    var englishTranslation:String? = null,
    @DocumentReference
    var language:Language,
):AuditMetadata() {
}

