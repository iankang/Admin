package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.models.requests.EditUserRequest
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    @PostMapping("/deleteuserById")
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
    @Operation(summary = "Edit a user by id", description = "Edits a user by id", tags = ["UserManagement"])
    @PutMapping("/editUserById")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('basic')or hasAuthority('editor')")
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
            if(userRequest.birthDate?.isNotBlank() == true || userRequest.birthDate?.isNotEmpty() == true){
                try {
                    // Define a custom date format pattern
                    val pattern = "dd/MM/yyyy"

                    // Create a DateTimeFormatter with the custom pattern
                    val formatter = DateTimeFormatter.ofPattern(pattern)
                    curentUser.user.birthDate =  LocalDate.parse(userRequest.birthDate, formatter)
                } catch (e:Exception){
                    logger.error("error parsing data: ${e.message}")
                }

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
}