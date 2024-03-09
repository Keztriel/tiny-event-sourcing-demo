package ru.quipy.taskmanager.controller

import org.springframework.data.mongodb.core.query.where
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.taskmanager.api.UserAggregate
import ru.quipy.taskmanager.api.UserCreatedEvent
import ru.quipy.taskmanager.logic.UserAggregateState
import ru.quipy.taskmanager.logic.create
//import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.taskmanager.projections.User
import ru.quipy.taskmanager.projections.UserCacheRepository
//import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
        val userCacheRepository: UserCacheRepository,
) {


    @PostMapping("/{userName}")
    fun createUser(@PathVariable userName: String, @RequestParam nickname: String, @RequestParam password: String ): UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), nickname, userName, password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String) : UserAggregateState? {
        return userEsService.getState(UUID.fromString(userId))
    }

    @GetMapping()
    fun getAllUsers() : MutableList<User> {
        return userCacheRepository.findAll()
    }

    @GetMapping("/find/{username}")
    fun FindUser(@PathVariable username: String) : UserAggregateState? {
        val userId = userCacheRepository.findByUsername(username)?.userId
        userId?.let { return userEsService.getState(it) } ?: return null
    }

//    @PostMapping("/{userId}/invite/{projectId}")
//    fun createUser(@PathVariable userId: UUID, @PathVariable projectId: UUID): UserInvitedEvent {
//        return userEsService.update(userId) { it.invite(projectId) }
//    }
}