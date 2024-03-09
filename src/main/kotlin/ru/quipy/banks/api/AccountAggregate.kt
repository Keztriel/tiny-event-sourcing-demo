package ru.quipy.banks.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "aggregate-account")
class AccountAggregate : Aggregate