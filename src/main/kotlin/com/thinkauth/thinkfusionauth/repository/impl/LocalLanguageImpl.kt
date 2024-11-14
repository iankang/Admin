package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.LocalLanguage
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.LocalLanguageRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class LocalLanguageImpl(
    private val localLanguageRepository: LocalLanguageRepository
) :DataOperations<LocalLanguage>{
    override fun itemExistsById(id: String): Boolean {
        return localLanguageRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<LocalLanguage>> {
        val page = PageRequest.of(page, size)

        val everything = localLanguageRepository.findAll(page)

        return PagedResponse(
            everything.content,
            everything.number,
            everything.totalElements,
            everything.totalPages
        )
    }

    override fun getItemById(id: String): LocalLanguage {
       return localLanguageRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        return localLanguageRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        localLanguageRepository.deleteAll()
    }

    override fun updateItem(id: String, item: LocalLanguage): LocalLanguage? {
        val local = getItemById(id)
        local.languageName = item.languageName
        local.code = item.code
        local.country = item.country
        local.classification = item.classification
        return createItem(local)
    }

    override fun createItem(item: LocalLanguage): LocalLanguage {
        return localLanguageRepository.save(item)
    }


    fun existsByLanguageName(languageName:String):Boolean{
        return localLanguageRepository.existsByLanguageName(languageName)
    }

    fun getAllLanguages(): MutableList<LocalLanguage> {
        return localLanguageRepository.findAll()
    }

    fun getLanguageByLanguageName(
        languageName:String
    ): List<LocalLanguage> {
        return localLanguageRepository.findAllByLanguageName(languageName)
    }
}