package ru.quipy.taskmanager.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskmanager.api.UserAggregate
import ru.quipy.taskmanager.api.UserCreatedEvent
import ru.quipy.taskmanager.logic.UserAggregateState
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserExistenceCache(
    private val userCacheRepository: UserCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
) {
    private val logger: Logger = LoggerFactory.getLogger(UserExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users::cache") {
            `when`(UserCreatedEvent::class) { event ->
                userCacheRepository.save(User(event.userId, event.userName, event.nickname))
            }
        }
    }

}

@Document("users-cache")
data class User(
    @Id
    val userId: UUID,
    val username: String,
    val nickname: String,
)

@Repository
interface UserCacheRepository: MongoRepository<User,UUID> {
    fun findByUsername(username: String): User?
}