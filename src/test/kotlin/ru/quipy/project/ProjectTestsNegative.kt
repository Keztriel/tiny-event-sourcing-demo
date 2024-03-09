package ru.quipy.project

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.taskmanager.api.ProjectAggregate
import ru.quipy.taskmanager.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.taskmanager.logic.ProjectAggregateState
import ru.quipy.taskmanager.logic.UserAggregateState
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectTestsNegative {
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
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(ProjectTestsNegative.testId)), "aggregate-project")
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(ProjectTestsNegative.userId)), "aggregate-user")

    }

    @Test
    fun `getNonexistentProject`() {
        val project = projectEsService.getState(testId)
        Assertions.assertEquals(project, null)
    }

}