package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.ConstituencyEntity
import com.thinkauth.thinkfusionauth.entities.CountyEntity
import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.repository.UserRepository
import com.thinkauth.thinkfusionauth.repository.impl.ConstituencyImpl
import com.thinkauth.thinkfusionauth.repository.impl.CountyServiceImple
import com.thinkauth.thinkfusionauth.utils.toUserEntity
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserManagementService(
    private val fusionAuthClient: FusionAuthClient,
    private val userRepository: UserRepository,
    private val countyServiceImple: CountyServiceImple,
    private val constituencyImpl: ConstituencyImpl
) {

    private val logger: Logger = LoggerFactory.getLogger(UserManagementService::class.java)
    fun loggedInUser(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        logger.info("authentication: {}", authentication)
        if (authentication != null && authentication.isAuthenticated) {
            logger.info("authenticated_user: {}", authentication)
            val username = authentication.principal as String
            logger.info("converted email: {}", username)
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
            return username
        }
        return null
    }


    fun fetchUserByEmail(email: String): User? {
        val userResponse = fusionAuthClient.retrieveUserByLoginId(email)
        return if (userResponse.wasSuccessful()) {
            userResponse.successResponse.user
        } else {
            return null
        }
    }

    fun fetchUserEntityByEmailFromFusionAuth(email: String): UserEntity? {
        val user = fetchUserByEmail(email)
        if (user != null) {
            return user.toUserEntity()
        }
        return null
    }

    fun addUserFromFusionAuthByEmail(email: String) {
        val userENt = fetchUserEntityByEmailFromFusionAuth(email)

        if (userENt != null ) {
            val userExists = userRepository.existsByEmail(email)
            if(!userExists) {
                addUserEntity(userENt)
            }
        }
    }


    fun addUserEntity(userEntity: UserEntity): UserEntity {
        return if(!userRepository.existsByEmail(userEntity.email ?: "")) {
             userRepository.save(userEntity)
        } else{
            userEntity
        }
    }



    fun fetchUserEntityByEmail(email: String): UserEntity {
        return userRepository.findByEmail(email)
    }

    fun fetchAllUsersWithEmail(email: String): List<UserEntity> {
        return userRepository.findAllByEmail(email)
    }

    fun fetchAllUsers(): MutableList<UserEntity> {
        return userRepository.findAll()
    }

    fun countUserInstancesEmail(email: String):Long{
        return userRepository.countByEmail(email)
    }
    fun countUserInstancesUsername(username: String):Long{
        return userRepository.countByUsername(username)
    }

    fun deleteAllUsers(){
        logger.warn("deleting all useUrs")
        userRepository.deleteAll()
    }

    fun deleteAllUsersByEmail(email:String){
        userRepository.deleteAllByEmail(email)
    }

    fun addAllUsers(allUsers:List<UserEntity>): MutableList<UserEntity> {
       return  userRepository.saveAll(allUsers)
    }
    fun fetchLoggedInUserEntity(): UserEntity {
        val email = loggedInUser()
        logger.info("fetchLoggedInUserEntity")
        if (!userRepository.existsByEmail(email!!)) {
            logger.info("user not available, creating")
            addUserFromFusionAuthByEmail(email)
        }
        return fetchUserEntityByEmail(email)
    }

    fun fetchAllCounties(): MutableList<CountyEntity> {
        return countyServiceImple.getAll()
    }

    fun fetchConstituenciesByCountyId(
        countyId:Int
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByCountyId(countyId)
    }

    fun fetchConstituenciesByCountyName(
        countyName:String
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByCountyName(countyName)
    }

    fun fetchByConstituencyName(
        constituencyName:String
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByConstituencyName(constituencyName)
    }
}