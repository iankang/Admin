package com.thinkauth.thinkfusionauth.entities

import net.minidev.json.annotate.JsonIgnore
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Conversation(
    var userEmail:String,
    var botInformationId:String,
    var conversationTitle:String?= null
//    @DBRef
//    @JsonIgnore
//    var messages:MutableList<Message>
):AuditMetadata() {

}