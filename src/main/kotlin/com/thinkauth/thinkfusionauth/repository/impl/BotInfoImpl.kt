package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.BotInformationRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BotInfoImpl(
    private val botInformationRepository: BotInformationRepository
) : DataOperations<BotInformation> {

    private val logger = LoggerFactory.getLogger(BotInfoImpl::class.java)
    override fun itemExistsById(id: String): Boolean {
        return botInformationRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<BotInformation>> {

        val paged = PageRequest.of(page, size)
        val everything = botInformationRepository.findAll(paged)
        val content = everything.content
        return PagedResponse(
            content, everything.number, everything.totalElements, everything.totalPages
        )
    }

    override fun createItem(item: BotInformation): BotInformation {
        return botInformationRepository.save(item)
    }

    override fun getItemById(id: String): BotInformation {
        val botInfo = botInformationRepository.findById(id).get()
        logger.info("fetch item by Id: {}", botInfo)
        return botInfo
    }

    override fun deleteItemById(id: String) {
        logger.info("deleting item by id: {}", id)
        botInformationRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        botInformationRepository.deleteAll()
    }

    override fun updateItem(id: String, item: BotInformation): BotInformation? {
        if (itemExistsById(id)) {
            val botRepo = getItemById(id)
            botRepo.botName = item.botName
            botRepo.botLogoUrl = item.botLogoUrl
            botRepo.botUrl = item.botUrl
            botRepo.botPort = item.botPort
            botRepo.botPath = item.botPath
            botRepo.botIsAvailable = item.botIsAvailable
            botRepo.business = item.business
            botRepo.botType = item.botType
            return createItem(botRepo)
        }
        return null
    }

    fun createForBotInformationAndBusiness(
        botInformation: BotInformation, business: Business
    ): BotInformation {
        botInformation.business = business
        return createItem(botInformation)
    }
}