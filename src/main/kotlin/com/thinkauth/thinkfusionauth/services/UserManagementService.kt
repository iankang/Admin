package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import com.thinkauth.thinkfusionauth.repository.UserRepository
import com.thinkauth.thinkfusionauth.utils.toUserEntity
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserManagementService(
    private val fusionAuthClient: FusionAuthClient,
    private val userRepository: UserRepository
) {

    fun loggedInUser():String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication !is AnonymousAuthenticationToken) {
            val userPrincipal = authentication.principal as String
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
            return userPrincipal
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
        if(!userRepository.existsByEmail(email!!)){

            addUserFromFusionAuthByEmail(email)
        }
        return fetchUserEntityByEmail(email)
    }

}