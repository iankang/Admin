package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.Conversation
import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.ConversationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ConversationImpl(
    val conversationRepository: ConversationRepository
):DataOperations<Conversation> {
    override fun itemExistsById(id: String): Boolean {
        return conversationRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<Conversation>> {

       val paged = PageRequest.of(page, size)
        val section = conversationRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): Conversation {
        return conversationRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        conversationRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        conversationRepository.deleteAll()
    }

    override fun updateItem(id: String, item: Conversation): Conversation? {

        val converse = getItemById(id)
        converse.userEmail = item.userEmail
        converse.botInformationId= item.botInformationId

        return createItem(converse)
    }

    override fun createItem(item: Conversation): Conversation {
       return conversationRepository.save(item)
    }

    fun addMessage(
        botInformationId:String,
        userId:String,
        message: Message
        ){

        val convo = fetchByUserIdAndBotInformationId(userId, botInformationId)

        createItem(convo)
    }


    fun getConversation(
        botInformationId:String,
        userId:String,
    ): Conversation? {
        val conversation = conversationRepository.findByUserEmailAndBotInformationId(userId, botInformationId)
        return conversation
    }

    fun fetchByUserIdAndBotInformationId(
        userId:String,
        botInformationId: String
    ): Conversation {
        return conversationRepository.findByUserEmailAndBotInformationId(userId, botInformationId)
    }

    fun fetchUserConversations(
        userId: String
    ): List<Conversation> {
        return conversationRepository.findAllByUserEmail(userId)
    }

    fun existsByUserId(
        userId: String
    ): Boolean {
        return conversationRepository.existsByUserEmail(userId)
    }

    fun existByConversationId(
        conversationId:String
    ): Boolean {
        return conversationRepository.existsById(conversationId)
    }
}