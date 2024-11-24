package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.RelevantLanguages
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.RelevantLanguagesRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class RelevantLanguagesImpl(
    private val relevantLanguagesRepository: RelevantLanguagesRepository
) :DataOperations<RelevantLanguages>{
    override fun itemExistsById(id: String): Boolean {
        return relevantLanguagesRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<RelevantLanguages>> {
        val paged = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")))
        val section = relevantLanguagesRepository.findAll(paged)
        return PagedResponse(
            section.content, section.number, section.totalElements, section.totalPages
        )
    }

    override fun getItemById(id: String): RelevantLanguages {
        return relevantLanguagesRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
       relevantLanguagesRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        relevantLanguagesRepository.deleteAll()
    }

    override fun updateItem(id: String, item: RelevantLanguages): RelevantLanguages? {
       val entity = getItemById(id)
        entity.languageName = item.languageName
        entity.code = item.code
        entity.country = item.country
        entity.classification = item.classification
        return createItem(entity)
    }

    override fun createItem(item: RelevantLanguages): RelevantLanguages {
        return relevantLanguagesRepository.save(item)
    }

    fun countRelevantLanguages(): Long {
        return relevantLanguagesRepository.count()
    }
}