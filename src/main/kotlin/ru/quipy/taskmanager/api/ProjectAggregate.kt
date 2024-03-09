package ru.quipy.taskmanager.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "aggregate-project")
class ProjectAggregate : Aggregate