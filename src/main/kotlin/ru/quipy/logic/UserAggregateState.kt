package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import javax.annotation.meta.TypeQualifierNickname

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var nickname: String
    lateinit var username: String
    lateinit var password: String
    var projects = mutableSetOf<UUID>()

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        nickname= event.nickname
        username = event.userName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userInvitedToProjectApply(event: UserInvitedEvent) {
        projects.add(event.projectId)
        updatedAt = createdAt
    }
}
