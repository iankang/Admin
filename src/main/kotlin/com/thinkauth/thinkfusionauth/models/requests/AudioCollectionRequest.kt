package com.thinkauth.thinkfusionauth.models.requests

class AudioCollectionRequest(
    var sentence:String,
    var languageId:String,
    var englishTranslation:String?= null,
    var businessId:String? = null
) {
}