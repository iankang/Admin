package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.entities.Conversation
import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.ConversationImpl
import com.thinkauth.thinkfusionauth.services.ConversationService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.mongodb.repository.Query
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.websocket.server.PathParam

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/Conversation")
@Tag(name = "Conversation", description = "Manage Conversations")
class ConversationController(
    private val conversationImpl: ConversationImpl,
    private val userManagementService: UserManagementService
) {

    @Operation(
        summary = "Add a conversation", description = "adds a conversation", tags = ["Conversation"]
    )
    @PostMapping("/createConversation/{botId}")
    fun createAConversation(
        @PathVariable("botId") botId:String
    ): ResponseEntity<Conversation> {
        val userId = userManagementService.loggedInUser()!!
        return try {
            ResponseEntity(
                conversationImpl.createItem(
                    Conversation(
                        userId,
                        botId
                    )
                ), HttpStatus.OK
            )
        }catch (e:Exception){
            throw Exception(e)
        }
    }

    @Operation(
        summary = "Fetch all conversations", description = "fetches all conversations", tags = ["Conversation"]
    )
    @GetMapping("/allConversations")
    fun getAllConversations(
        @RequestParam("page", defaultValue = "0") page:Int = 0,
        @RequestParam("size", defaultValue = "10") size:Int = 10
    ): ResponseEntity<PagedResponse<List<Conversation>>> {
        return try { ResponseEntity(conversationImpl.findEverythingPaged(page, size),HttpStatus.OK)}
        catch (e:Exception){
            throw Exception(e)
        }
    }

    @Operation(
        summary = "Fetch a conversation", description = "fetches a conversation", tags = ["Conversation"]
    )
    @GetMapping("/conversationById")
    fun fetchAConversationById(
        @RequestParam("conversationId", defaultValue = "0") conversationId:String
    ): ResponseEntity<Conversation> {
        return try {  ResponseEntity(conversationImpl.getItemById(conversationId), HttpStatus.OK)}
        catch (e:Exception){
            throw Exception(e)
        }
    }
    @Operation(
        summary = "Fetch conversations for a user", description = "fetches a conversation for a user", tags = ["Conversation"]
    )
    @GetMapping("/conversationsByUser")
    fun fetchConversationsForUser(userId:String): ResponseEntity<List<Conversation>> {
        return try {  ResponseEntity(conversationImpl.fetchUserConversations(userId),HttpStatus.OK)}
        catch (e:Exception){
            throw Exception(e)
        }
    }
    @Operation(
        summary = "Fetch conversations for a logged in user", description = "fetches a conversation for a logged in user", tags = ["Conversation"]
    )
    @GetMapping("/conversationsByLoggedInUser")
    fun fetchConversationsForLoggedInUser(): ResponseEntity<List<Conversation>> {
        val userId = userManagementService.loggedInUser()!!
        return try {  ResponseEntity(conversationImpl.fetchUserConversations(userId),HttpStatus.OK)}
        catch (e:Exception){
            throw Exception(e)
        }
    }

    @Operation(
        summary = "Delete a conversation", description = "Deletes a conversation", tags = ["Conversation"]
    )
    @DeleteMapping("/deleteConversationById")
    fun deleteAConversation(
        conversationId:String
    ): ResponseEntity<Unit> {
        return try {  ResponseEntity(conversationImpl.deleteItemById(conversationId),HttpStatus.OK)}
        catch (e:Exception){
            throw Exception(e)
        }
    }


}