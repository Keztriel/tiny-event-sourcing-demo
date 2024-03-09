package ru.quipy.taskmanager.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.taskmanager.api.*
import ru.quipy.taskmanager.logic.*
import ru.quipy.core.EventSourcingService
import ru.quipy.taskmanager.projections.Project
import ru.quipy.taskmanager.projections.ProjectCacheRepository
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectCacheRepository: ProjectCacheRepository,
) {

    @GetMapping()
    fun getAllProjects(): MutableList<Project> {
        return projectCacheRepository?.findAll()
    }

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @PostMapping("/{projectId}/rename")
    fun renameProject(@PathVariable projectId: UUID, @RequestParam name: String, @RequestParam editorId: String): ProjectRenamedEvent {
        return projectEsService.update(projectId) { it.rename(name, editorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: String) : ProjectAggregateState? {
        return projectEsService.getState(UUID.fromString(projectId))
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun deleteTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskDeletedEvent {
        return projectEsService.update(projectId) { it.removeTask(taskId) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/executors/{executorId}")
    fun assignTaskToExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable executorId: UUID)
    : TaskAssignedEvent {
        return projectEsService.update(projectId) { it.assignTaskToExecutor(taskId, executorId) }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}/executors/{executorId}")
    fun unassignTaskToExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable executorId: UUID)
    : TaskUnassignedEvent {
        return projectEsService.update(projectId) {it.unassignTaskToExecutor(taskId, executorId)}
    }

    @PostMapping("/{projectId}/tags/{tagName}")
    fun createTag(@PathVariable projectId: UUID, @PathVariable tagName: String) : TagCreatedEvent {
        return projectEsService.update(projectId) { it.createTag(tagName) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/tags/{tagId}")
    fun assignTagToTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable tagId: UUID)
    : TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(tagId, taskId)
        }
    }

    @PostMapping("/{userId}/invite/{projectId}")
    fun createUser(@PathVariable userId: UUID, @PathVariable projectId: UUID): UserInvitedEvent {
        return projectEsService.update(projectId) { it.invite(userId) }
    }
}