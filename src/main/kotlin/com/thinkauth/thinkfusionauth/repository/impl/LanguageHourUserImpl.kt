package com.thinkauth.thinkfusionauth.repository.impl

import com.thinkauth.thinkfusionauth.entities.LanguageHourUserEntity
import com.thinkauth.thinkfusionauth.entities.LanguageHoursEntity
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.LanguageHourUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class LanguageHourUserImpl(
    private val languageHourUserRepository: LanguageHourUserRepository
):DataOperations<LanguageHourUserEntity> {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun itemExistsById(id: String): Boolean {
       return languageHourUserRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<LanguageHourUserEntity>> {
        val paged = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")))
        val section = languageHourUserRepository.findAll(paged)
        return PagedResponse(
            section.content, section.number, section.totalElements, section.totalPages
        )
    }

    override fun getItemById(id: String): LanguageHourUserEntity {
       return languageHourUserRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        languageHourUserRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        languageHourUserRepository.deleteAll()
    }

    override fun updateItem(id: String, item: LanguageHourUserEntity): LanguageHourUserEntity? {
        TODO("Not yet implemented")
    }

    fun updateLanguageHourUser(email:String,languageHourUserEntity: LanguageHourUserEntity){
        if(userAlreadyExists(email)){
            logger.info("user exists.")
            val langEnt = findUserByEmail(email)
            langEnt?.email = email
            langEnt?.userEntity = languageHourUserEntity.userEntity
            langEnt?.totalCount = languageHourUserEntity.totalCount
            langEnt?.acceptedCount = languageHourUserEntity.acceptedCount
            langEnt?.acceptedDuration = languageHourUserEntity.acceptedDuration
            langEnt?.rejectedCount = languageHourUserEntity.rejectedCount
            langEnt?.rejectedDuration = languageHourUserEntity.rejectedDuration
            langEnt?.pendingCount = languageHourUserEntity.pendingCount
            langEnt?.pendingDuration = languageHourUserEntity.pendingDuration

            createItem(langEnt!!)
        } else {
            logger.info("creating a new user: ${languageHourUserEntity}")
            createItem(languageHourUserEntity)
        }
    }

    override fun createItem(item: LanguageHourUserEntity): LanguageHourUserEntity {
        return languageHourUserRepository.save(item)
    }

    fun userAlreadyExists(email:String): Boolean {
        return languageHourUserRepository.existsByEmail(email)
    }

    fun findUserByEmail(email:String): LanguageHourUserEntity? {
        return languageHourUserRepository.findByEmail(email)
    }

    fun getAllHourUsers(): MutableList<LanguageHourUserEntity> {
        return languageHourUserRepository.findAll()
    }

    fun numberOfUserHoursLogged(email: String):Long{
        return languageHourUserRepository.countByEmail(email)
    }
}