package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.SentenceUserIgnore
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.SentenceIgnoreRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import javax.xml.crypto.Data
@Component
class SentenceIgnoreImpl(
    private val sentenceIgnoreRepository: SentenceIgnoreRepository
):DataOperations<SentenceUserIgnore> {
    override fun itemExistsById(id: String): Boolean {
        return sentenceIgnoreRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<SentenceUserIgnore>> {
        val paged = PageRequest.of(page, size)
        val section = sentenceIgnoreRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): SentenceUserIgnore {
        return sentenceIgnoreRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        sentenceIgnoreRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        sentenceIgnoreRepository.deleteAll()
    }

    override fun updateItem(id: String, item: SentenceUserIgnore): SentenceUserIgnore? {
        val sentenceIgnoreUser = getItemById(id)
        sentenceIgnoreUser.sentenceId = item.sentenceId
        sentenceIgnoreUser.userId = item.userId
        return createItem(sentenceIgnoreUser)
    }

    override fun createItem(item: SentenceUserIgnore): SentenceUserIgnore {
        return sentenceIgnoreRepository.save(item)
    }

    fun findAllByUserId(userId:String): List<SentenceUserIgnore> {
        return sentenceIgnoreRepository.findAllByUserId(userId)
    }

    fun existsBySentenceIdAndUserId(
        sentenceId:String,
        userId: String
    ): Boolean {
        return sentenceIgnoreRepository.existsBySentenceIdAndUserId(sentenceId, userId)
    }

    fun findBySentenceIdAndUserid(
        sentenceId:String,
        userId: String
    ): SentenceUserIgnore {
        return sentenceIgnoreRepository.findBySentenceIdAndUserId(sentenceId, userId)
    }

    fun countIgnoredSentencesByUserId(
        userId: String
    ): Long {
        return sentenceIgnoreRepository.countByUserId(userId)
    }
}