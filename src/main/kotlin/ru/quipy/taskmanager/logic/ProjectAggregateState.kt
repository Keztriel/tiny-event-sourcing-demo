package ru.quipy.taskmanager.logic

import ru.quipy.taskmanager.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    lateinit var editorId: String
    var users = mutableSetOf<UUID>()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        editorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectRenamedApply(event: ProjectRenamedEvent) {
        projectTitle = event.title
        editorId = event.editorId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf(), mutableSetOf())
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskRenamedApply(event: TaskRenamedEvent) {
        val tags = tasks[event.taskId]?.tagsAssigned ?: mutableSetOf()
        val executors = tasks[event.taskId]?.executors ?: mutableSetOf()
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, tags, executors)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskDeletedApply(event: TaskDeletedEvent) {
        tasks.remove(event.taskId)
    }

    @StateTransitionFunc
    fun taskAssignedApply(event: TaskAssignedEvent) {
        tasks[event.taskId]?.executors?.add(event.executorId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskUnassignedApply(event: TaskUnassignedEvent) {
        tasks[event.taskId]?.executors?.remove(event.executorId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userInvitedToProjectApply(event: UserInvitedEvent) {
        users.add(event.userId)
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tagsAssigned: MutableSet<UUID>,
    val executors: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
