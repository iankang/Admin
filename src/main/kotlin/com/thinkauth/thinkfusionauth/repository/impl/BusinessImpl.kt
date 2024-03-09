package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.BusinessRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BusinessImpl(
    private val businessRepository: BusinessRepository
):DataOperations<Business> {
    override fun itemExistsById(id: String): Boolean {
        return businessRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<Business>> {

        val page = PageRequest.of(page, size)

        val everything = businessRepository.findAll(page)

        return PagedResponse(
            everything.content,
            everything.number,
            everything.totalElements,
            everything.totalPages
        )

    }

    override fun getItemById(id: String): Business {
        return businessRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        businessRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        businessRepository.deleteAll()
    }

    override fun updateItem(id: String, item: Business): Business? {
        if(itemExistsById(id)) {
            val dbItem = getItemById(id)
            dbItem.apply {
                businessName = item.businessName
                businessImageProfile = item.businessImageProfile
            }
            return createItem(dbItem)
        }
        return null
    }

    override fun createItem(item: Business): Business {
        return businessRepository.save(item)
    }

}