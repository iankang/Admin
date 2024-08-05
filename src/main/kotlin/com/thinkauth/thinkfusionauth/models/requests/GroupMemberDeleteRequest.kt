package com.thinkauth.thinkfusionauth.models.requests

import java.util.UUID

data class GroupMemberDeleteRequest(
    var groupId:UUID,
    var userIds:List<UUID>
)
