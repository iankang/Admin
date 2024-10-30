package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.ConstituencyEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.ConstituencyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ConstituencyImpl(
    private val constituencyRepository: ConstituencyRepository
):DataOperations<ConstituencyEntity> {
    override fun itemExistsById(id: String): Boolean {
        return constituencyRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<ConstituencyEntity>> {
        val paged = PageRequest.of(page,size)
        val section = constituencyRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): ConstituencyEntity {
        return constituencyRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        constituencyRepository.deleteById(id)
    }

    override fun deleteAllItems() {
       constituencyRepository.deleteAll()
    }

    override fun updateItem(id: String, item: ConstituencyEntity): ConstituencyEntity? {
        val constit = getItemById(id)
        constit.constituencyName = item.constituencyName
        constit.countyId = item.countyId
        constit.wardName = item.wardName
        constit.countyName = item.countyName
        return createItem(constit)
    }

    override fun createItem(item: ConstituencyEntity): ConstituencyEntity {
        return constituencyRepository.save(item)
    }

    fun saveAll(items:List<ConstituencyEntity>): MutableList<ConstituencyEntity> {
        return constituencyRepository.saveAll(items)
    }
    fun count(): Long {
        return constituencyRepository.count()
    }

    fun getByCountyId(countyId:Int): List<ConstituencyEntity> {
        return constituencyRepository.findAllByCountyId(countyId)
    }

    fun getByCountyName(countyName:String): List<ConstituencyEntity> {
        return constituencyRepository.findAllByCountyName(countyName)
    }

    fun getByConstituencyName(constituencyName:String): List<ConstituencyEntity> {
        return constituencyRepository.findAllByConstituencyName(constituencyName)
    }

    fun getAllConstituencies(): MutableList<ConstituencyEntity> {
        return constituencyRepository.findAll()
    }
}