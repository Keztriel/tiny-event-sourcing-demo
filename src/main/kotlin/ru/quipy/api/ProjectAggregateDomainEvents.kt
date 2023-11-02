package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

//const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
//const val USER_INVITED_EVENT = "USER_INVITED_EVENT"
const val PROJECT_RENAMED_EVENT = "PROJECT_RENAMED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"
const val TASK_RENAMED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_ASSIGNED_EVENT = "TASK_ASSIGNED_EVENT"
const val TASK_UNASSIGNED_EVENT = "TASK_UNASSIGNED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_RENAMED_EVENT)
class ProjectRenamedEvent(
    val projectId: UUID,
    val title: String,
    val editorId: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate> (
    name = PROJECT_RENAMED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_CREATED_EVENT)
class TagCreatedEvent(
    val projectId: UUID,
    val tagId: UUID,
    val tagName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TAG_ASSIGNED_TO_TASK_EVENT)
class TagAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val tagId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val projectId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_RENAMED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_ASSIGNED_EVENT)
class TaskAssignedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val executorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_ASSIGNED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_UNASSIGNED_EVENT)
class TaskUnassignedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val executorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_UNASSIGNED_EVENT,
    createdAt = createdAt
)