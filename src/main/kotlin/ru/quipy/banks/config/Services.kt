package ru.quipy.banks.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.banks.api.AccountAggregate
import ru.quipy.banks.logic.AccountAggregateState
import ru.quipy.banks.projections.AccountOwnersProjection
import ru.quipy.banks.projections.TransactionProjection
import ru.quipy.core.EventSourcingService
import java.util.*

@Service
class Services @Autowired constructor(
    val accountEsService: EventSourcingService<UUID, AccountAggregate, AccountAggregateState>,
    val accountOwnersProjection: AccountOwnersProjection,
    val transactionProjection: TransactionProjection,

)