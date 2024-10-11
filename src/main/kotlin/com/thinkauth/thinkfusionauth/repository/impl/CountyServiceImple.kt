package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.CountyEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.CountyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class CountyServiceImple(
    private val countyRepository: CountyRepository
):DataOperations<CountyEntity> {
    override fun itemExistsById(id: String): Boolean {
       return countyRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<CountyEntity>> {
        val paged = PageRequest.of(page, size)
        val section = countyRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): CountyEntity {
        return countyRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        countyRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        countyRepository.deleteAll()
    }

    override fun updateItem(id: String, item: CountyEntity): CountyEntity? {
       val county = getItemById(id)
        county.countyId = item.countyId
        county.name =item.name
        county.capital = item.capital

        return createItem(county)
    }

    override fun createItem(item: CountyEntity): CountyEntity {
        return countyRepository.save(item)
    }

    fun count(): Long {
        return countyRepository.count()
    }

    fun saveAll(items:List<CountyEntity>): MutableList<CountyEntity> {
        return countyRepository.saveAll(items)
    }

    fun getAll(): MutableList<CountyEntity> {
        return countyRepository.findAll()
    }
}