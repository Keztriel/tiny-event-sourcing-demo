package ru.quipy.taskmanager.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.taskmanager.api.ProjectAggregate
import ru.quipy.taskmanager.api.ProjectCreatedEvent
import ru.quipy.taskmanager.api.UserInvitedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectExistenceCache(
    private val projectCacheRepository: ProjectCacheRepository,
    private val projectMembersCacheRepository: ProjectMembersCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
) {
    private val logger: Logger = LoggerFactory.getLogger(ProjectExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::cache") {
            `when` (ProjectCreatedEvent::class) { event ->
                projectCacheRepository.save(Project(event.projectId, event.creatorId, event.title))
                projectMembersCacheRepository.save(ProjectMembership(event.projectId, UUID.fromString(event.creatorId)))
                logger.info(("Update project cache, create project ${event.projectId}"))
            }

            `when` (UserInvitedEvent::class) { event ->
                projectMembersCacheRepository.save(ProjectMembership(event.projectId, event.userId))

            }
        }

//        subscriptionsManager.createSubscriber(UserAggregate::class, "")
    }
}

@Document("projects-cache")
data class Project(
    @Id
    val projectId: UUID,
    val creatorId: String,
    val name: String,
)

@Document("project-membership-cache")
data class ProjectMembership(
    @Id
    val projectId: UUID,
    val userId: UUID,
)

@Repository
interface ProjectCacheRepository: MongoRepository<Project, UUID>

@Repository
interface ProjectMembersCacheRepository: MongoRepository<ProjectMembership, UUID>