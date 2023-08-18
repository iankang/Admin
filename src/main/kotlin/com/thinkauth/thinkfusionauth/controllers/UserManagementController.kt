package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.api.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
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

    @Operation(summary = "get a user by email", description = "Gets a user by email", tags = ["UserManagement"])
    @GetMapping("/fetchUserByEmail")
    @PreAuthorize("hasAuthority('admin')")
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
}