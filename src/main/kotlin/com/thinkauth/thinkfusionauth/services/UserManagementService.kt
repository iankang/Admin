package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.entities.ConstituencyEntity
import com.thinkauth.thinkfusionauth.entities.CountyEntity
import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.models.responses.UserData
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


    @TrackExecutionTime
    fun fetchUserByEmail(email: String): User? {
        val userResponse = fusionAuthClient.retrieveUserByLoginId(email)
        return if (userResponse.wasSuccessful()) {
            userResponse.successResponse.user
        } else {
            return null
        }
    }

    @TrackExecutionTime
    fun fetchLocalUserByEmail(email:String):UserEntity?{
        return userRepository.findByEmail(email)
    }

    @TrackExecutionTime
    fun convertUserEntityByEmailFromFusionAuth(email: String): UserEntity? {
        val user = fetchUserByEmail(email)
        if (user != null) {
            return user.toUserEntity()
        }
        return null
    }

    @TrackExecutionTime
    fun addUserFromFusionAuthByEmail(email: String) {
        val userENt = convertUserEntityByEmailFromFusionAuth(email)

        if (userENt != null ) {
            val userExists = userRepository.existsByEmail(email)
            if(!userExists) {
                logger.info("user does not exist: $email")
                addUserEntity(userENt)
            } else {
                logger.info("updating user: $email")
                updateUserEntity(userRepository.findByEmail(email),userENt)
            }
        }
    }

    @TrackExecutionTime
    fun addUserEntity(userEntity: UserEntity): UserEntity {
        return if(!userRepository.existsByEmail(userEntity.email ?: "")) {
             userRepository.save(userEntity)
        } else{
            userEntity
        }
    }

    @TrackExecutionTime
    fun updateUserEntity(
        originalUserEntity: UserEntity,
        destinationUserEntity:UserEntity
    ): UserEntity {
        logger.info("updating user entity")
        originalUserEntity.firstName = destinationUserEntity.firstName
        originalUserEntity.middleName = destinationUserEntity.middleName
        originalUserEntity.lastName = destinationUserEntity.lastName
        originalUserEntity.username = destinationUserEntity.username
        originalUserEntity.birthDate = destinationUserEntity.birthDate
        originalUserEntity.email = destinationUserEntity.email
        originalUserEntity.imageUrl = destinationUserEntity.imageUrl
        originalUserEntity.mobilePhone = destinationUserEntity.mobilePhone
        originalUserEntity.languageId = destinationUserEntity.languageId
        originalUserEntity.genderState = destinationUserEntity.genderState
        originalUserEntity.ageGroup = destinationUserEntity.ageGroup
        originalUserEntity.nationalId = destinationUserEntity.nationalId
        originalUserEntity.countyId = destinationUserEntity.countyId
        originalUserEntity.constituency = destinationUserEntity.constituency

        return userRepository.save(originalUserEntity)

    }
    @TrackExecutionTime
    fun fetchUserEntityByEmail(email: String): UserEntity {
        return userRepository.findByEmail(email)
    }

    @TrackExecutionTime
    fun getUserByMobile(mobile:String): List<UserEntity>? {
        logger.info("mobile: ${mobile}")
        return userRepository.findByMobilePhone(mobile)
    }
    @TrackExecutionTime
    fun fetchAllUsersWithEmail(email: String): List<UserEntity> {
        return userRepository.findAllByEmail(email)
    }
    @TrackExecutionTime
    fun fetchAllUsers(): MutableList<UserEntity> {
        return userRepository.findAll()
    }
    @TrackExecutionTime
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
    @TrackExecutionTime
    fun fetchLoggedInUserEntity(): UserEntity {
        val email = loggedInUser()
        logger.info("fetchLoggedInUserEntity")
        if (!userRepository.existsByEmail(email!!)) {
            logger.info("user not available, creating")
            addUserFromFusionAuthByEmail(email)
        }
        return fetchUserEntityByEmail(email)
    }
    @TrackExecutionTime
    fun fetchAllCounties(): MutableList<CountyEntity> {
        return countyServiceImple.getAll()
    }
    @TrackExecutionTime
    fun fetchAllConstituencies():MutableList<ConstituencyEntity>{
        return constituencyImpl.getAllConstituencies()
    }

    @TrackExecutionTime
    fun fetchConstituenciesByCountyId(
        countyId:Int
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByCountyId(countyId)
    }
    @TrackExecutionTime
    fun fetchConstituenciesByCountyName(
        countyName:String
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByCountyName(countyName)
    }
    @TrackExecutionTime
    fun fetchByConstituencyName(
        constituencyName:String
    ): List<ConstituencyEntity> {
        return constituencyImpl.getByConstituencyName(constituencyName)
    }

    @TrackExecutionTime
    fun getUserData(email: String):UserData?{
        val user = fetchUserByEmail(email)
        return if(user != null){
          UserData(
              constituencyId = if(user.data.containsKey("constituencyId")) user.data["constituencyId"].toString() else null,
              constituencyName = if(user.data.containsKey("constituencyName")) user.data["constituencyName"].toString() else null,
              countyId = if(user.data.containsKey("countyId")) user.data["countyId"]?.toString()?.toIntOrNull() else null,
              countyName = if(user.data.containsKey("countyName")) user.data["countyName"]?.toString() else null,
              dialectId = if(user.data.containsKey("dialectId")) user.data["dialectId"]?.toString() else null,
              educationLevel = if(user.data.containsKey("education_level")) user.data["education_level"]?.toString() else null,
              employmentState = if(user.data.containsKey("employment")) user.data["employment"]?.toString() else null,
              genderState = if(user.data.containsKey("gender")) user.data["gender"]?.toString() else null,
              languageId = if(user.data.containsKey("languageId")) user.data["languageId"]?.toString() else null,
              nationalId = if(user.data.containsKey("nationalId")) user.data["nationalId"]?.toString()?.toLong() else null,
          )
        }else{
            null
        }
    }
}