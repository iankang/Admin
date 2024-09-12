package com.thinkauth.thinkfusionauth.services

import com.inversoft.error.Errors
import com.inversoft.rest.ClientResponse
import com.thinkauth.thinkfusionauth.models.requests.UserGroupRequest
import com.thinkauth.thinkfusionauth.repository.UserRepository
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.Group
import io.fusionauth.domain.GroupMember
import io.fusionauth.domain.User
import io.fusionauth.domain.api.GroupRequest
import io.fusionauth.domain.api.GroupResponse
import io.fusionauth.domain.api.MemberDeleteRequest
import io.fusionauth.domain.api.MemberRequest
import io.fusionauth.domain.api.MemberResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class  UserGroupsManagementService(
    private val fusionAuthClient: FusionAuthClient,
    private val userRepository: UserRepository
) {

    fun addGroup(groupRequest: UserGroupRequest): ClientResponse<GroupResponse, Errors>? {
        val groupObject = Group(groupRequest.groupName)
        groupObject.data["description"] = groupRequest.groupDescription
       return  fusionAuthClient.createGroup(UUID.randomUUID(),GroupRequest(groupObject))
    }

    fun getGroups(): ClientResponse<GroupResponse, Void>? {
        return fusionAuthClient.retrieveGroups()
    }

    fun deleteGroup(
        groupId:UUID
    ): ClientResponse<Void, Errors>? {
        return fusionAuthClient.deleteGroup(groupId)
    }

    fun addMember(
        groupId: UUID,
        user:User
    ): ClientResponse<MemberResponse, Errors>? {
        val groupMember = GroupMember()
        groupMember.groupId = groupId
        groupMember.user = user
        groupMember.userId = user.id

        val memberRequest = MemberRequest(groupId, mutableListOf(groupMember))
        return fusionAuthClient.createGroupMembers(memberRequest)
    }

    fun removeMemberByMemberId(
        memberIds:List<UUID>
    ): ClientResponse<Void, Errors>? {
        val memberRequest = MemberDeleteRequest()
        memberRequest.memberIds = memberIds

        return fusionAuthClient.deleteGroupMembers(memberRequest)
    }

    fun removeMemberByGroupIdAndUserId(
        groupId: UUID,
        userId:List<UUID>
    ): ClientResponse<Void, Errors>? {
        val memberRequest = MemberDeleteRequest()
        memberRequest.members[groupId] = userId
        return fusionAuthClient.deleteGroupMembers(memberRequest)
    }
}