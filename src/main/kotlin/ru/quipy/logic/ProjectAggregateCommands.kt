package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.rename(title: String, editorId: String): ProjectRenamedEvent {
    return ProjectRenamedEvent(projectId = this.getId(), title = title, editorId = editorId)
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.removeTask(taskId: UUID): TaskDeletedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }
    return TaskDeletedEvent(projectId = this.getId(), taskId = taskId)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.assignTaskToExecutor(taskId: UUID, executorId: UUID): TaskAssignedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }
    if (tasks[taskId]?.executors?.contains(executorId) == true) {
        throw IllegalArgumentException("User with ID: $executorId is already an executor of Task $taskId")
    }
    return TaskAssignedEvent(projectId = this.getId(), taskId = taskId, executorId = executorId)
}

fun ProjectAggregateState.unassignTaskToExecutor(taskId: UUID, executorId: UUID): TaskUnassignedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }
    if (tasks[taskId]?.executors?.contains(executorId) == false) {
        throw IllegalArgumentException("User with ID: $executorId is not an executor of Task $taskId")
    }
    return TaskUnassignedEvent(projectId = this.getId(), taskId = taskId, executorId = executorId)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exist: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}