package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.DialectRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class DialectImpl(
    private val dialectRepository: DialectRepository,

):DataOperations<Dialect> {
    override fun itemExistsById(id: String): Boolean {
       return dialectRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<Dialect>> {
        val paged = PageRequest.of(page, size)
        val section = dialectRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): Dialect {
        return dialectRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        dialectRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        dialectRepository.deleteAll()
    }

    override fun updateItem(id: String, item: Dialect): Dialect? {
        val dialect = getItemById(id)
        dialect.dialectName = item.dialectName
        dialect.language = item.language
        return createItem(dialect)
    }

    override fun createItem(item: Dialect): Dialect {
        return dialectRepository.save(item)
    }

    fun countDialectsByLanguageName(languageName:String): Long {
        return dialectRepository.countAllByLanguageLanguageName(languageName)
    }

    fun getDialectsByLanguageName(languageName:String): List<Dialect> {
        return dialectRepository.findAllByLanguageLanguageName(languageName)
    }

    fun dialectCount(): Long {
        return dialectRepository.count()
    }

    fun existsByDialectName(
        dialectName:String
    ): Boolean {
        return dialectRepository.existsByDialectName(dialectName)
    }
}