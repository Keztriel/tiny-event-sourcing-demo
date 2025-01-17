package ru.quipy.taskmanager.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.taskmanager.api.ProjectAggregate
import ru.quipy.taskmanager.api.TagCreatedEvent
import ru.quipy.taskmanager.api.TaskCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "demo-subs-stream"
)
class AnnotationBasedProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        logger.info("Task created: {}", event.taskName)
    }

    @SubscribeEvent
    fun tagCreatedSubscriber(event: TagCreatedEvent) {
        logger.info("Tag created: {}", event.tagName)
    }
}