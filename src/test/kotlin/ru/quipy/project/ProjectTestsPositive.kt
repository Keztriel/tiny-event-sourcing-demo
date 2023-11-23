package ru.quipy.project

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.DemoApplication
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.net.URI
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectTestsPositive {
    companion object {
        private val testId = UUID.randomUUID()
        private val userId = UUID.randomUUID()
    }

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun init() {
        cleanDatabase()
    }

    fun cleanDatabase() {
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(testId)), "aggregate-project")
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(userId)), "aggregate-user")

    }

    @Test
    fun `createProject`() {
        val title = "Toast"
        projectEsService.create { it.create(testId, title=title, userId.toString()) }
        val state = projectEsService.getState(testId)
        Assertions.assertEquals(testId, state?.getId())
        Assertions.assertEquals(title, state?.projectTitle)
    }

    @Test
    fun `createAndRenameProject`() {
        val title = "Tess"
        val newTitle = "Test"
        projectEsService.create { it.create(testId, title=title, userId.toString()) }
        projectEsService.update(testId) { it.rename(newTitle, userId.toString())}
        val state = projectEsService.getState(testId)

        Assertions.assertEquals(newTitle, state?.projectTitle)
    }

    @Test
    fun `createTasksWithTags`() {
        val title = "Toast"
        val taskNames = arrayOf("Bread", "Butter", "Jam", "Honey")
        var taskCreated = mutableMapOf<String, TaskCreatedEvent>()
        val tagNames = arrayOf("optional")
        var tagCreated = mutableMapOf<String, TagCreatedEvent>()

        projectEsService.create { it.create(testId, title=title, userId.toString()) }
        tagNames.forEach {tag ->
//            tagCreated.plus(Pair(tag, projectEsService.update(testId) { it.createTag(tag)}))
            tagCreated[tag] = projectEsService.update(testId) { it.createTag(tag)}
        }
        taskNames.forEach { task ->
//            taskCreated.plus(Pair(task, projectEsService.update(testId) { it.addTask(task)}))
            taskCreated[task] = projectEsService.update(testId) { it.addTask(task)}
        }

        val tagId = tagCreated["optional"]?.tagId ?: throw NotFoundException()
        projectEsService.update(testId) {
            it.assignTagToTask(tagId, taskCreated?.get("Jam")!!.taskId)
        }
        projectEsService.update(testId) {
            it.assignTagToTask(tagId, taskCreated?.get("Honey")!!.taskId)
        }

        val state = projectEsService.getState(testId)

        Assertions.assertEquals(testId, state?.getId())
        Assertions.assertEquals(title, state?.projectTitle)
        tagCreated.forEach { (tag, e) ->
            Assertions.assertEquals(tag, state?.projectTags?.get(e.tagId)?.name)
        }
        taskCreated.forEach { (task, e) ->
            Assertions.assertEquals(task, state?.tasks?.get(e.taskId)?.name)
        }
        Assertions.assertTrue(state?.tasks?.get(taskCreated.get("Jam")?.taskId)?.tagsAssigned?.contains(tagId) == true)
        Assertions.assertTrue(state?.tasks?.get(taskCreated.get("Honey")?.taskId)?.tagsAssigned?.contains(tagId) == true)
    }

    @Test
    fun `createAndAssignTask`() {
        projectEsService.create { it.create(testId, "Toast", "Testo") }
        userEsService.create { it.create(userId, "Biba", "boba", "dva") }

        userEsService.update(userId) { it.invite(testId) }

        val user = userEsService.getState(userId)
        Assertions.assertTrue(true == user?.projects?.contains(testId))

        val taskCreated = projectEsService.update(testId) { it.addTask("Bread") }
        projectEsService.update(testId) { it.assignTaskToExecutor(taskCreated.taskId, userId)}

        val project = projectEsService.getState(testId)
        Assertions.assertTrue(true == project?.tasks?.get(taskCreated.taskId)?.executors?.contains(userId))
    }
}
