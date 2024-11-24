package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.LanguageMetricsEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.LanguageMetricsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class LanguageMetricsImpl(
    private val languageMetricsRepository: LanguageMetricsRepository
):DataOperations<LanguageMetricsEntity> {
    override fun itemExistsById(id: String): Boolean {
        return languageMetricsRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<LanguageMetricsEntity>> {
        val paged = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")))
        val section = languageMetricsRepository.findAll(paged)
        return PagedResponse(
            section.content, section.number, section.totalElements, section.totalPages
        )
    }

    override fun getItemById(id: String): LanguageMetricsEntity {
        return languageMetricsRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
       languageMetricsRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        languageMetricsRepository.deleteAll()
    }

    override fun updateItem(id: String, item: LanguageMetricsEntity): LanguageMetricsEntity? {
        val entity = getItemById(id)
        entity.languageId = item.languageId
        entity.languageName = item.languageName
        entity.sentenceCount = item.sentenceCount
        entity.recordingCount = item.recordingCount

        return createItem(entity)
    }

    override fun createItem(item: LanguageMetricsEntity): LanguageMetricsEntity {
       return languageMetricsRepository.save(item)
    }

    fun updateItem(item: LanguageMetricsEntity){
        val entity = languageMetricsRepository.findByLanguageId(item.languageId ?: "")
        entity.languageName = item.languageName
        entity.recordingCount = item.recordingCount
        entity.sentenceCount = item.sentenceCount

        createItem(entity)
    }
    @TrackExecutionTime
    fun getAllItems(): MutableList<LanguageMetricsEntity> {
        return languageMetricsRepository.findAll()
    }

    @TrackExecutionTime
    fun getLanguageMetricsCount(): Long {
        return languageMetricsRepository.count()
    }

    @TrackExecutionTime
    fun getByLanguageId(languageId:String): LanguageMetricsEntity {
        return languageMetricsRepository.findByLanguageId(languageId)
    }

    @TrackExecutionTime
    fun existsByLanguageId(languageId:String): Boolean {
        return languageMetricsRepository.existsByLanguageId(languageId)
    }
    @TrackExecutionTime
    fun removeExistingMetric(languageId: String){
        languageMetricsRepository.deleteByLanguageId(languageId)
    }

}