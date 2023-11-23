package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectExistenceCache(
    private val projectCacheRepository: ProjectCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger: Logger = LoggerFactory.getLogger(ProjectExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::cache") {
            `when` (ProjectCreatedEvent::class) { event ->
                projectCacheRepository.save(Project(event.projectId, event.creatorId))
                logger.info(("Update project cache, create project ${event.projectId}"))
            }
        }
    }
}

@Document("projects-cache")
data class Project(
    @Id
    val projectId: UUID,
    val creatorId: String
)

@Repository
interface ProjectCacheRepository: MongoRepository<Project, UUID>