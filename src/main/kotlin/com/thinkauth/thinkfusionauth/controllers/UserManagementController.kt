package com.thinkauth.thinkfusionauth.controllers


import com.inversoft.error.Errors
import com.thinkauth.thinkfusionauth.entities.ConstituencyEntity
import com.thinkauth.thinkfusionauth.entities.CountyEntity
import com.thinkauth.thinkfusionauth.entities.UserEntity
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.models.requests.EditUserRequest
import com.thinkauth.thinkfusionauth.models.requests.ProfileInfoRequest
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import com.thinkauth.thinkfusionauth.repository.impl.ConstituencyImpl
import com.thinkauth.thinkfusionauth.repository.impl.CountyServiceImple
import com.thinkauth.thinkfusionauth.services.ConversationService
import com.thinkauth.thinkfusionauth.services.DialectService
import com.thinkauth.thinkfusionauth.services.UserManagementService
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.api.UserRequest
import io.fusionauth.domain.api.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/userManagement")
@Tag(name = "UserManagement", description = "This manages users in the system.")
class UserManagementController(
    private val fusionAuthClient: FusionAuthClient,
    @Value("\${fusionauth.applicationId}")
    private val applicationId:String,
    @Value("\${fusionauth.tenantId}")
    private val tenantId:String,
    private val dialectService: DialectService,
    private val userManagementService: UserManagementService,
    private val conversationService: ConversationService,
    private val countyServiceImple: CountyServiceImple,
    private val constituencyImpl: ConstituencyImpl
) {
    private val logger:Logger = LoggerFactory.getLogger(UserManagementController::class.java)
    @Operation(summary = "get a user by email", description = "Gets a user by email", tags = ["UserManagement"])
    @GetMapping("/fetchUserByEmail")
    fun getUserByEmail(
        @RequestParam("email") email: String?
    ): ResponseEntity<FusionApiResponse<UserResponse>> {
        val userResponse = fusionAuthClient.retrieveUserByEmail(email)
        return if(userResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(userResponse.status,userResponse.successResponse,null), HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(userResponse.status,null,userResponse.errorResponse), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "get a user entity by email", description = "Gets a user entity by email", tags = ["UserManagement"])
    @GetMapping("/fetchUserEntityByEmail")
    fun getLocalUserByEmail(email: String?): ResponseEntity<UserEntity> {
        return ResponseEntity(userManagementService.fetchLocalUserByEmail(email!!),HttpStatus.OK)
    }
    @Operation(summary = "get a user by username", description = "Gets a user by username", tags = ["UserManagement"])
    @GetMapping("/fetchUserByUsername")
    fun getUserByUsername(
        @RequestParam("username") username: String?
    ): ResponseEntity<FusionApiResponse<UserResponse>> {
        val userResponse = fusionAuthClient.retrieveUserByUsername(username)
        return if(userResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(userResponse.status,userResponse.successResponse,null), HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(userResponse.status,null,userResponse.errorResponse), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "deactivate a user by id", description = "Deactivates a user by id", tags = ["UserManagement"])
    @PostMapping("/deactivateUserByUserId")
    @PreAuthorize("hasAuthority('admin')")
    fun deactivateUserByUserId(
        @RequestParam("userId") userId: String?
    ): ResponseEntity<FusionApiResponse<Void>> {
        val deactivateUserResponse = fusionAuthClient.deactivateUser(UUID.fromString(userId))
       return if(deactivateUserResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(deactivateUserResponse.status,deactivateUserResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(deactivateUserResponse.status,null,deactivateUserResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
       }
    }
    @Operation(summary = "reactivate a user by id", description = "Reactivates a user by id", tags = ["UserManagement"])
    @PostMapping("/reactivateUserByUserId")
    @PreAuthorize("hasAuthority('admin')")
    fun reactivateUserByUserId(
        @RequestParam("userId") userId: String?
    ): ResponseEntity<FusionApiResponse<UserResponse>> {
        val reactivateUserResponse = fusionAuthClient.reactivateUser(UUID.fromString(userId))
       return if(reactivateUserResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(reactivateUserResponse.status,reactivateUserResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(reactivateUserResponse.status,null,reactivateUserResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
       }
    }

    @Operation(summary = "Delete a user by id", description = "Deletes a user by id", tags = ["UserManagement"])
    @PostMapping("/deleteUserById")
    @PreAuthorize("hasAuthority('admin')")
    fun deleteUserById(
        @RequestParam("userId") userId: String?
    ): ResponseEntity<FusionApiResponse<Void>> {
        val deleteUserResponse = fusionAuthClient.deleteUser(UUID.fromString(userId))
        return if(deleteUserResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(deleteUserResponse.status,deleteUserResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(deleteUserResponse.status,null,deleteUserResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Delete all user data", description = "Deletes a user data", tags = ["UserManagement"])
    @PostMapping("/deleteUserData")
    fun deleteUserData(
        @RequestParam("email") email: String?
    ): ResponseEntity<FusionApiResponse<Void>> {

        val userResponse = fusionAuthClient.retrieveUserByEmail(email)
         return if(userResponse.wasSuccessful()){
             logger.info("delete local user first email: {}", email)
             userManagementService.deleteAllUsersByEmail(email!!)
             conversationService.deleteConversationsByUserEmail(email)
             conversationService.deleteAllBySender(email)

             val deleteUserResponse = fusionAuthClient.deleteUser(userResponse.successResponse.user.id)

              if(deleteUserResponse.wasSuccessful()){
                 ResponseEntity(FusionApiResponse(deleteUserResponse.status,deleteUserResponse.successResponse,null),HttpStatus.OK)
             } else {
                 ResponseEntity(FusionApiResponse(deleteUserResponse.status,null,deleteUserResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
             }
        } else {
            ResponseEntity(FusionApiResponse(userResponse.status,null,userResponse.errorResponse), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    @Operation(summary = "Edit a user by id", description = "Edits a user by id", tags = ["UserManagement"])
    @PutMapping("/editUserById")
    @PreAuthorize("permitAll()")
    fun editUserByUserId(
        @RequestParam("userId", required = true)  userId: UUID,
        @RequestBody userRequest:EditUserRequest
    ): ResponseEntity<out FusionApiResponse<out UserResponse>> {
        val userResponse = fusionAuthClient.retrieveUser(userId)

       return  if(userResponse.wasSuccessful()){
            val curentUser = UserRequest(userResponse.successResponse.user)
            if(userRequest.email?.isNotBlank() == true || userRequest.email?.isNotEmpty() == true){
                curentUser.user.email = userRequest.email
            }
            if(userRequest.firstName?.isNotBlank() == true || userRequest.firstName?.isNotEmpty() == true){
                curentUser.user.firstName = userRequest.firstName
            }
            if(userRequest.fullName?.isNotBlank() == true || userRequest.fullName?.isNotEmpty() == true){
                curentUser.user.fullName = userRequest.fullName
            }
            if(userRequest.imageUrl?.isNotBlank() == true || userRequest.imageUrl?.isNotEmpty() == true){
                curentUser.user.imageUrl = URI(userRequest.imageUrl ?: "")
            }
            if(userRequest.lastName?.isNotBlank() == true || userRequest.lastName?.isNotEmpty() == true){
                curentUser.user.lastName = userRequest.lastName
            }
            if(userRequest.middleName?.isNotBlank() == true || userRequest.middleName?.isNotEmpty() == true){
                curentUser.user.middleName = userRequest.middleName
            }
            if(userRequest.mobilePhone?.isNotBlank() == true || userRequest.mobilePhone?.isNotEmpty() == true){
                curentUser.user.mobilePhone = userRequest.mobilePhone
            }
           if(userRequest.birthDate != null) {
               curentUser.user.birthDate = userRequest.birthDate
           }

           if(userRequest.gender != null){
               curentUser.user.data["gender"] = userRequest.gender!!.name
           }
           if(userRequest.ageRangeEnum != null){
               curentUser.user.data["ageRange"] = userRequest.ageRangeEnum!!.name
           }

           if(userRequest.educationLevel != null){
               curentUser.user.data["education_level"] = userRequest.educationLevel!!.name
           }

           if(userRequest.nationalId != null){
               curentUser.user.data["nationalId"] = userRequest.nationalId
           }
           if(userRequest.dialectId != null){
               if(dialectService.existsByDialectId(dialectId = userRequest.dialectId!!)) {
                   val dialect = dialectService.getDialectById(userRequest.dialectId!!)
                   curentUser.user.data["dialectId"] = dialect.id
                   curentUser.user.data["languageId"] = dialect.language?.id
               } else {
                   throw ResourceNotFoundException("dialect with id ${userRequest.dialectId} not found")
               }
           }

           if(userRequest.countyId != null){
               curentUser.user.data["countyId"] = userRequest.countyId
               if(countyServiceImple.existsByCode(userRequest.countyId!!)){
                   val county = countyServiceImple.getCountyByCode(userRequest.countyId!!)
                   curentUser.user.data["countyName"] = county.name
               }
           }

           if(userRequest.constituencyId != null){
               curentUser.user.data["constituencyId"] = userRequest.constituencyId

               if(constituencyImpl.itemExistsById(userRequest.constituencyId!!)){
                   val constituency = constituencyImpl.getItemById(userRequest.constituencyId!!)
                   curentUser.user.data["constituencyName"] = constituency.constituencyName
               }
           }
           if(userRequest.employment != null){
               curentUser.user.data["employment"] = userRequest.employment
           }

            val userEditedResponse = fusionAuthClient.updateUser(userId,curentUser)
            return if(userEditedResponse.wasSuccessful()) {
                ResponseEntity(
                    FusionApiResponse(userEditedResponse.status, userEditedResponse.successResponse, null),
                    HttpStatus.OK
                )
            }else{
                ResponseEntity(FusionApiResponse(userEditedResponse.status,null,userEditedResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
            }

        }  else {
            ResponseEntity(FusionApiResponse(userResponse.status,null,userResponse.errorResponse), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Add profile information", description = "adds profile information", tags = ["UserManagement"])
    @PostMapping("/addProfileInfo")
    @PreAuthorize("permitAll()")
    fun addUserProfileInformation(
        @RequestParam("userId", required = true)  userId: UUID,
        @RequestBody userProfileInfoRequest: ProfileInfoRequest
    ): ResponseEntity<out FusionApiResponse<out UserResponse>> {
        logger.info("input: $userProfileInfoRequest")
        val userResponse = fusionAuthClient.retrieveUser(userId)
        logger.info("userResponse ${userResponse.status}")
        return  if(userResponse.wasSuccessful()){
            var curentUser = UserRequest(userResponse.successResponse.user)
            logger.info("currentUser: $curentUser")
            if(userProfileInfoRequest.gender != null){
                curentUser.user.data["gender"] = userProfileInfoRequest.gender!!.name
            }
            if(userProfileInfoRequest.ageRangeEnum != null){
                curentUser.user.data["ageRange"] = userProfileInfoRequest.ageRangeEnum
            }
            if(userProfileInfoRequest.nationalId != null){
                curentUser.user.data["nationalId"] = userProfileInfoRequest.nationalId
            }
            if(userProfileInfoRequest.educationLevel != null){
                curentUser.user.data["education_level"] = userProfileInfoRequest.educationLevel!!.name
            }

            if(userProfileInfoRequest.countyId != null){
                curentUser.user.data["countyId"] = userProfileInfoRequest.countyId
            }

            if(userProfileInfoRequest.constituencyId  != null){
                curentUser.user.data["constituencyId"] = userProfileInfoRequest.constituencyId
            }
            if(userProfileInfoRequest.dialectId != null){
                if(dialectService.existsByDialectId(dialectId = userProfileInfoRequest.dialectId!!)) {
                    val dialect = dialectService.getDialectById(userProfileInfoRequest.dialectId!!)
                    logger.info("dialect: ${dialect.dialectName}")
                    curentUser.user.data["dialectId"] = dialect.id
                    curentUser.user.data["languageId"] = dialect.language?.id
                } else {
                    throw ResourceNotFoundException("dialect with id ${userProfileInfoRequest.dialectId} not found")
                }
            }
            val userEditedResponse = fusionAuthClient.updateUser(userId,curentUser)
            logger.info("userResponse: ${userEditedResponse.status}")
            logger.info("userResponse: ${userEditedResponse.errorResponse}")
            logger.info("userResponse: ${userEditedResponse.exception}")
            return if(userEditedResponse.wasSuccessful()) {
                ResponseEntity(
                    FusionApiResponse(userEditedResponse.status, userEditedResponse.successResponse, null),
                    HttpStatus.OK
                )
            }else{
                ResponseEntity(FusionApiResponse(userEditedResponse.status,null, Errors().addGeneralError(userResponse.status.toString(),userResponse.exception.message)),HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }  else {
            ResponseEntity(FusionApiResponse(userResponse.status,null,userResponse.errorResponse), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "get all users", description = "Gets all users", tags = ["UserManagement"])
    @GetMapping("/fetchAllUsers")
    fun fetchAllUsers(): ResponseEntity<MutableList<UserEntity>> {
        return ResponseEntity(userManagementService.fetchAllUsers(), HttpStatus.OK)
    }

    @Operation(summary = "get all counties", description = "Gets all counties", tags = ["UserManagement"])
    @GetMapping("/fetchAllCounties" )
    fun fetchAllCounties(): ResponseEntity<MutableList<CountyEntity>> {
        return ResponseEntity(userManagementService.fetchAllCounties(),HttpStatus.OK)
    }

    @Operation(summary = "get all constituencies by countyId", description = "Gets all constituencies by county id", tags = ["UserManagement"])
    @GetMapping("/fetchConstituenciesByCountyId" )
    fun fetchConstituenciesByCountyId(
        @RequestParam("countyId", required = true) countyId:Int
    ): ResponseEntity<List<ConstituencyEntity>> {
        return ResponseEntity(
            userManagementService.fetchConstituenciesByCountyId(countyId),
            HttpStatus.OK
        )
    }

    @Operation(summary = "get all constituencies", description = "Gets all constituencies", tags = ["UserManagement"])
    @GetMapping("/fetchAllConstituencies" )
    fun fetchAllConstituencies(
    ): ResponseEntity<List<ConstituencyEntity>> {
        return ResponseEntity(
            userManagementService.fetchAllConstituencies(),
            HttpStatus.OK
        )
    }

    @Operation(summary = "get user by phone", description = "Gets by phone", tags = ["UserManagement"])
    @GetMapping("/fetchUserByPhone" )
    fun getUserDetailsByMobile(
        mobile:String
    ): ResponseEntity<List<UserEntity?>> {

        return ResponseEntity(
            userManagementService.getUserByMobile(mobile),
            HttpStatus.OK
        )
    }
}