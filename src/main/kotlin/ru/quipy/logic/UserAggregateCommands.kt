package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun UserAggregateState.create(id: UUID, nickname: String, username: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            nickname = nickname,
            userName = username,
            password = password,
    )
}

fun UserAggregateState.invite(projectId: UUID): UserInvitedEvent {
    return UserInvitedEvent(
            userId = this.getId(),
            projectId = projectId,
    )
}