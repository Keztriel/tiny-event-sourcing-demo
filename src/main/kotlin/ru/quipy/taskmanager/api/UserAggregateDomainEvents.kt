package ru.quipy.taskmanager.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_INVITED_EVENT = "USER_INVITED_EVENT"

// API
@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
        val userId: UUID,
        val nickname: String,
        val userName: String,
        val password: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
        name = USER_CREATED_EVENT,
        createdAt = createdAt,
)

//@DomainEvent(name = USER_INVITED_EVENT)
//class UserInvitedEvent(
//        val userId: UUID,
//        val projectId: UUID,
//        createdAt: Long = System.currentTimeMillis(),
//) : Event<UserAggregate>(
//        name = USER_INVITED_EVENT,
//        createdAt = createdAt
//)
