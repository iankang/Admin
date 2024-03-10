package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.MessageRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MessagesImpl(
    private val messageRepository: MessageRepository
) : DataOperations<Message> {

    private val logger = LoggerFactory.getLogger(MessagesImpl::class.java)
    override fun itemExistsById(id: String): Boolean {
        return messageRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<Message>> {
        val paged = PageRequest.of(page, size)
        val items = messageRepository.findAll(paged)
        return PagedResponse(
            items.content, items.number, items.totalElements, items.totalPages
        )
    }

    fun findAllByConversationId(
        conversationId:String
    ): List<Message> {
        return messageRepository.findAllByConversationId(conversationId)
    }
    override fun getItemById(id: String): Message {
        return messageRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        messageRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        messageRepository.deleteAll()
    }

    override fun updateItem(id: String, item: Message): Message {
        val entity = messageRepository.findByIdOrNull(id)
        entity?.messageType = item.messageType
        entity?.content = item.content
        entity?.sender = item.sender
        return createItem(entity!!)
    }

    override fun createItem(item: Message): Message {
        logger.info("Message item creation: {}", item.toString())
        return messageRepository.save(item)
    }


}