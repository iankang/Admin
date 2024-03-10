package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.models.requests.MessageEnum
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Message(
    var content:String,
    var sender:String,
    var messageType:MessageEnum
):AuditMetadata() {

    var conversationId:String? = null
}