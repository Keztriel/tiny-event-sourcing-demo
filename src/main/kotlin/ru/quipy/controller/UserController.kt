package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*
import javax.annotation.meta.TypeQualifierNickname

@RestController
@RequestMapping("/users")
class UserController(
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{userName}")
    fun createUser(@PathVariable userName: String, @RequestParam nickname: String, @RequestParam password: String ): UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), nickname, userName, password) }
    }

    @PostMapping("/{userId}/invite/{projectId}")
    fun createUser(@PathVariable userId: UUID, @PathVariable projectId: UUID): UserInvitedEvent {
        return userEsService.update(userId) { it.invite(projectId) }
    }
}