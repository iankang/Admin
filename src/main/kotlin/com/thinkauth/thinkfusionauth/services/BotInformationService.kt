package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.BotInformationRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.BotInformationRepository
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.utils.BucketName
import com.thinkauth.thinkfusionauth.utils.NetworkUtils.isServiceAvailable
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class BotInformationService(
    private val businessService: BusinessService,
    private val fileManagerService: StorageService,
    private val botInfoImpl: BotInfoImpl,
    @Value("\${minio.bucket}")
    private val thinkResources: String,
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
            botType = botInformationRequest.botType,
            business = business
        )
        return botInformation
    }

    fun addBotProfilePicture(botId:String, file: MultipartFile):BotInformation{

        val path = thinkResources+ File.separator+ BucketName.BOT_PROFILE_PIC.name+ File.separator+file.originalFilename
        val response = fileManagerService.uploadFile(thinkResources,path,file.inputStream)
        val botInformation = botInfoImpl.getItemById(botId)
        botInformation.botLogoUrl = file.originalFilename
        return botInfoImpl.createItem(botInformation)
    }

    fun botExistsById(id:String): Boolean {
        return botInfoImpl.itemExistsById(id)
    }
}