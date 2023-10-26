package com.thinkauth.thinkfusionauth.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
class BotChat(
    sessionId:String? = null,
    email:String? = null,
    query:String? = null,
    response:String? = null,

):AuditMetadata() {
}