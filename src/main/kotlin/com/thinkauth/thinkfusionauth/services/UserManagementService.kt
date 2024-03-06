package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import com.thinkauth.thinkfusionauth.repository.UserRepository
import com.thinkauth.thinkfusionauth.utils.FileProcessingHelper
import com.thinkauth.thinkfusionauth.utils.toUserEntity
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserManagementService(
    private val fusionAuthClient: FusionAuthClient,
    private val userRepository: UserRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(UserManagementService::class.java)
    fun loggedInUser():String? {
        val authentication = SecurityContextHolder.getContext().authentication
        logger.info("authentication: {}",authentication)
        if (authentication != null && authentication.isAuthenticated){
            logger.info("authenticated_user: {}", authentication)
            val username = authentication.principal as String
            logger.info("converted email: {}", username)
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
            return username
        }
        return null
    }


    fun fetchUserByEmail(email:String): User? {
        val userResponse = fusionAuthClient.retrieveUserByEmail(email)
        return if(userResponse.wasSuccessful()){
            userResponse.successResponse.user
        } else{
            return null
        }
    }

    fun fetchUserEntityByEmailFromFusionAuth(email: String): UserEntity? {
        val user = fetchUserByEmail(email)
        if(user != null){
            return user.toUserEntity()
        }
        return null
    }

    fun addUserFromFusionAuthByEmail(email: String){
        val userENt = fetchUserEntityByEmailFromFusionAuth(email)
        if(userENt != null){
            addUserEntity(userENt)
        }
    }


    fun addUserEntity(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    fun fetchUserEntityByEmail(email:String):UserEntity{
        return userRepository.findByEmail(email)
    }

    fun fetchLoggedInUserEntity(): UserEntity {
        val email =loggedInUser()
        logger.info("fetchLoggedInUserEntity")
        if(!userRepository.existsByEmail(email!!)){
            logger.info("user not available, creating")
            addUserFromFusionAuthByEmail(email)
        }
        return fetchUserEntityByEmail(email)
    }

}