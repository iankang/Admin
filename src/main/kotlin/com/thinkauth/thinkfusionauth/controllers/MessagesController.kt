package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.models.requests.MessageRequest
import com.thinkauth.thinkfusionauth.repository.impl.MessagesImpl
import com.thinkauth.thinkfusionauth.services.ConversationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/Message")
@Tag(name = "Message", description = "Manage Messages")
class MessagesController(
    private val messagesImpl: MessagesImpl,
    private val conversationService: ConversationService,

) {
    @Operation(
        summary = "Fetch all messages", description = "fetches all messages", tags = ["Message"]
    )
    @GetMapping("/allConversations/{conversationId}")
    fun getMessagesFromConversation(
        @PathVariable("conversationId") conversationId:String
    ): ResponseEntity<List<Message>> {
        return ResponseEntity(messagesImpl.findAllByConversationId(conversationId),HttpStatus.OK)
    }

    @Operation(
        summary = "Talk to bot in conversation", description = "Talks to bot in conversation", tags = ["Message"]
    )
    @PostMapping("/talkToBotInConversation")
    fun continueConversation(
        @RequestParam("conversationId") conversationId:String,
        @RequestBody message: MessageRequest
    ): ResponseEntity<List<Message>> {
        return ResponseEntity( conversationService.userCreateMessageInConversation(conversationId, message), HttpStatus.OK)
    }
}