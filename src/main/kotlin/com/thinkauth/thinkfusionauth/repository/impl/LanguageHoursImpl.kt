package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.LanguageHoursRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class LanguageHoursImpl(
    private val languageHoursRepository:LanguageHoursRepository
):DataOperations<LanguageHoursEntity> {
    override fun itemExistsById(id: String): Boolean {
        return languageHoursRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<LanguageHoursEntity>> {
        val paged = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")))
        val section = languageHoursRepository.findAll(paged)
        return PagedResponse(
            section.content, section.number, section.totalElements, section.totalPages
        )
    }

    override fun getItemById(id: String): LanguageHoursEntity {
        return languageHoursRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        languageHoursRepository.deleteById(id)
    }

    override fun deleteAllItems() {
       languageHoursRepository.deleteAll()
    }

    override fun updateItem(languageId: String, item: LanguageHoursEntity): LanguageHoursEntity? {
        val entity = getLanguageByLanguageId(languageId)
        entity.languageId = item.languageId
        entity.languageName = item.languageName
        entity.totalCount = item.totalCount
        entity.totalDuration = item.totalDuration
        entity.acceptedCount = item.acceptedCount
        entity.acceptedDuration = item.acceptedDuration
        entity.rejectedCount = item.rejectedCount
        entity.rejectedDuration = item.rejectedDuration
        entity.pendingCount = item.pendingCount
        entity.pendingDuration = item.pendingDuration
        return createItem(entity)
    }

    override fun createItem(item: LanguageHoursEntity): LanguageHoursEntity {
        return languageHoursRepository.save(item)
    }

    fun getLanguageByLanguageId(languageId:String): LanguageHoursEntity {
        return languageHoursRepository.findByLanguageId(languageId)
    }

    fun saveAll(list:List<LanguageHoursEntity>): MutableList<LanguageHoursEntity> {
        return languageHoursRepository.saveAll(list)
    }

    fun getAll(): MutableList<LanguageHoursEntity> {
        return languageHoursRepository.findAll()
    }

    fun delete(languageHoursEntity: LanguageHoursEntity){
        return languageHoursRepository.delete(languageHoursEntity)
    }

    fun deleteByLanguageId(languageId:String){
        return languageHoursRepository.deleteByLanguageId(languageId)
    }
}