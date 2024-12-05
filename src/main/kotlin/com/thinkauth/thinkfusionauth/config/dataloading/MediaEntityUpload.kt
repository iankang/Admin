package com.thinkauth.thinkfusionauth.config.dataloading

import com.thinkauth.thinkfusionauth.models.responses.UserData
import com.thinkauth.thinkfusionauth.services.MediaEntityService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Component
@Order(3)
class MediaEntityUpload(
    private val mediaEntityService: MediaEntityService,
    private val userManagementService: UserManagementService
):CommandLineRunner {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String?) {
        logger.info("media count: ${mediaEntityService.getMediaEntityNullCount()}")
        var index = 0
        val userMap:MutableMap<String,UserData?> = mutableMapOf()
        val manyMedias = mediaEntityService.findMediaEntitiesWithDialectIdNull(0,2000).item.map {
            var userData:UserData? = null
            logger.info("index: ${index}")
            index+=1
             if(userMap.containsKey(it.owner.email)){
                userData = userMap[it.owner.email]
            } else{
                val uza = userManagementService.getUserData(it.owner.email!!)
                userMap[it.owner.email!!] = uza
                 userData = uza
            }

            it.constituencyId = userData?.constituencyId
            it.constituencyName = userData?.constituencyName
            it.countyId = userData?.countyId
            it.countyName= userData?.countyName
            it.dialectId = userData?.dialectId
            it.educationLevel = userData?.educationLevel
            it.employmentState = userData?.employmentState
            it.genderState = userData?.genderState
            it.languageId = userData?.languageId
            it.nationalId = userData?.nationalId
//            mediaEntityService.saveMediaEntity(it)
                it
        }
        logger.info("manyMediasSize: ${manyMedias.size}")
        mediaEntityService.saveManyMediaEntities(manyMedias)
    }
}