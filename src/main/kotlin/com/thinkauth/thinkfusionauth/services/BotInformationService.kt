package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.BotInformationRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.BotInformationRepository
import com.thinkauth.thinkfusionauth.utils.NetworkUtils.isServiceAvailable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class BotInformationService(
    private val businessService: BusinessService
) {

    fun addBotInformation(botInformationRequest: BotInformationRequest, business: Business): BotInformation {


        var botAvailability: Boolean? = null

        if (botInformationRequest.botUrl != null && botInformationRequest.botPort != null) {
            botAvailability = isServiceAvailable(
                serviceHost = botInformationRequest.botUrl, servicePort = botInformationRequest.botPort!!
            )
        }

        val botInformation = BotInformation(
            botName = botInformationRequest.botName,
            botDescription = botInformationRequest.botDescription,
            botUrl = botInformationRequest.botUrl,
            botPort = botInformationRequest.botPort,
            botLogoUrl = botInformationRequest.botLogoUrl,
            botPath = botInformationRequest.botPath,
            botIsAvailable = botAvailability,
            business = business
        )
        return botInformation
    }
}