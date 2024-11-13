package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.PromptEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.PromptRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class PromptImpl(
    private val promptRepository: PromptRepository
) :DataOperations<PromptEntity>{
    override fun itemExistsById(id: String): Boolean {
        return promptRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<PromptEntity>> {
        val paged = PageRequest.of(page, size)
        val section = promptRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): PromptEntity {
        return promptRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        promptRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        promptRepository.deleteAll()
    }

    override fun updateItem(id: String, item: PromptEntity): PromptEntity? {
        val prompt = getItemById(id)
        prompt.url = item.url
        prompt.promptType = item.promptType
        prompt.title = item.title
        prompt.ageRangeEnum = item.ageRangeEnum
        prompt.genderState = item.genderState
        prompt.dialect = item.dialect
        prompt.business = item.business
        prompt.language = item.language
        prompt.description = item.description
        return createItem(item)
    }

    override fun createItem(item: PromptEntity): PromptEntity {
        return promptRepository.save(item)
    }
}