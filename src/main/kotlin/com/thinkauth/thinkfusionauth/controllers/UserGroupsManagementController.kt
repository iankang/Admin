package com.thinkauth.thinkfusionauth.controllers

import com.inversoft.error.Errors
import com.inversoft.rest.ClientResponse
import com.thinkauth.thinkfusionauth.models.requests.GroupMemberDeleteRequest
import com.thinkauth.thinkfusionauth.models.requests.UserGroupRequest
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import com.thinkauth.thinkfusionauth.services.UserGroupsManagementService
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.api.GroupResponse
import io.fusionauth.domain.api.MemberResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID
import kotlin.math.log

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/userGroupManagement")
@Tag(name = "UserGroup", description = "This manages groups users belong to in the system.")
class UserGroupsManagementController(
    private val fusionAuthClient: FusionAuthClient,
    private val userGroupsManagementService: UserGroupsManagementService
) {
    private val logger: Logger = LoggerFactory.getLogger(UserGroupsManagementController::class.java)

    @Operation(summary = "Create a user group", description = "Creates a user group", tags = ["UserGroup"])
    @PostMapping("/createUserGroup")
    @PreAuthorize("permitAll()")
    fun createUserGroup(
        @RequestBody groupRequest: UserGroupRequest
    ): ResponseEntity<FusionApiResponse<GroupResponse>> {

        val groupResponse = userGroupsManagementService.addGroup(groupRequest)
        return if (groupResponse?.wasSuccessful() == true) {
            logger.debug("successful: {}", groupResponse.getSuccessResponse())
            ResponseEntity(FusionApiResponse(groupResponse.status, groupResponse.successResponse, null), HttpStatus.OK)
        } else {
            ResponseEntity(
                FusionApiResponse(groupResponse?.status, null, groupResponse?.errorResponse),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }

    }

    @Operation(summary = "Gets user groups", description = "Gets user groups", tags = ["UserGroup"])
    @GetMapping("/getUserGroups")
    @PreAuthorize("permitAll()")
    fun getGroups(): ResponseEntity<FusionApiResponse<GroupResponse?>> {
        val groupResponse = userGroupsManagementService.getGroups()
        return if (groupResponse?.wasSuccessful() == true) {
            ResponseEntity(FusionApiResponse(groupResponse.status, groupResponse.successResponse, null), HttpStatus.OK)
        } else {
            ResponseEntity(
               FusionApiResponse(groupResponse?.status,null,Errors().addGeneralError(HttpStatus.INTERNAL_SERVER_ERROR.name,"error")),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    @Operation(summary = "Delete a user group", description = "Deletes A user-group", tags = ["UserGroup"])
    @DeleteMapping("/deleteUserGroup/{userGroupUUID}")
    @PreAuthorize("permitAll()")
    fun deleteGroup(
        @RequestParam("userGroupUUID") userGroupId:UUID
    ): ResponseEntity<FusionApiResponse<Void>> {
        val groupResponse = userGroupsManagementService.deleteGroup(userGroupId)
        return if(groupResponse?.wasSuccessful() == true){
            ResponseEntity(FusionApiResponse(groupResponse.status,groupResponse.successResponse,null), HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(groupResponse?.status, null, null), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Add a user to group", description = "Adds  A user to group", tags = ["UserGroup"])
    @PostMapping("/addGroupUser")
    @PreAuthorize("permitAll()")
    fun addUserToGroup(
        @RequestParam("userEmail", required = true) email:String,
        @RequestParam("groupId", required = true) groupId:UUID
    ): ResponseEntity<FusionApiResponse<MemberResponse>> {
        val userResponse = fusionAuthClient.retrieveUserByEmail(email)
        return when(userResponse.wasSuccessful()){
            true -> {
                logger.info("user found: {}", userResponse.getSuccessResponse().toString())
                val user = userResponse.successResponse.user
                val response = userGroupsManagementService.addMember(groupId, user)
                when(response?.wasSuccessful()){
                    true ->{
                        ResponseEntity(FusionApiResponse(response.status, response.successResponse,null),HttpStatus.OK)
                    }
                    false ->{
                        ResponseEntity(FusionApiResponse(response.status,null, response.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
                    }

                    null -> {
                        ResponseEntity(FusionApiResponse(response?.status,null, null),HttpStatus.INTERNAL_SERVER_ERROR)
                    }
                }
            }
            false -> {
                logger.info("user error: {}", userResponse.errorResponse.toString())
                 ResponseEntity(FusionApiResponse(userResponse.status,null, userResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
    @Operation(summary = "Delete a user from group using member ID", description = "Deletes  A user from group using member ID", tags = ["UserGroup"])
    @DeleteMapping("/deleteUserFromGroup")
    @PreAuthorize("permitAll()")
    fun deleteUserFromGroup(
       @RequestBody memberIds:List<UUID>
    ): ResponseEntity<FusionApiResponse<Void>> {
        val response = userGroupsManagementService.removeMemberByMemberId(memberIds)
        return when(response?.wasSuccessful()){
            true -> {
                ResponseEntity(FusionApiResponse(response.status,response.successResponse, null),HttpStatus.OK)
            }
            false -> {
                ResponseEntity(FusionApiResponse(response.status,null, null),HttpStatus.INTERNAL_SERVER_ERROR)
            }
            null -> {
                ResponseEntity(FusionApiResponse(response?.status,null, null),HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    @Operation(summary = "Delete a user from group using Group Id and UserIds", description = "Deletes  A user from group using Group Id and UserIds", tags = ["UserGroup"])
    @DeleteMapping("/deleteUserFromGroupUsingGroupIdAndUserIds")
    @PreAuthorize("permitAll()")
    fun deleteUserFromGroupByGroupIdAndUserIds(
        deleteRequest: GroupMemberDeleteRequest
    ):ResponseEntity<FusionApiResponse<Void>>{
        val response = userGroupsManagementService.removeMemberByGroupIdAndUserId(deleteRequest.groupId,deleteRequest.userIds)
        return when(response?.wasSuccessful()){
            true -> {
                ResponseEntity(FusionApiResponse(response.status,response.successResponse,null),HttpStatus.OK)
            }
            false -> {
                ResponseEntity(FusionApiResponse(response.status,null,null),HttpStatus.INTERNAL_SERVER_ERROR)
            }
            null -> {
                ResponseEntity(FusionApiResponse(response?.status,null,null),HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}