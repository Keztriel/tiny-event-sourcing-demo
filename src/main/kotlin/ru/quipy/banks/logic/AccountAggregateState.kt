package ru.quipy.banks.logic

import ru.quipy.banks.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class AccountAggregateState : AggregateState<UUID, AccountAggregate>{
    private lateinit var accountId: UUID
    var balance: Int = 0
    lateinit var ownerId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var removed: Boolean = false


    override fun getId() = accountId

    @StateTransitionFunc
    fun accountCreatedApply(event: AccountCreatedEvent) {
        accountId = event.accountId
        balance = 0
        ownerId = event.ownerId

        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun accountRemovedApply(event: AccountRemovedEvent) {
        updatedAt = event.createdAt
        removed = true
    }

    @StateTransitionFunc
    fun balanceChangedApply(event: BalanceChangedEvent){
        balance += event.amount

        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun transactionStartedApply(event: TransactionStartedEvent){
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun transactionWithdrawApply(event: TransactionWithdrawEvent){
        balance -= event.amount

        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun transactionDepositApply(event: TransactionDepositEvent){
        balance += event.amount

        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun transactionWithdrawFailedApply(event: TransactionWithdrawFailedEvent){
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun transactionDepositFailedEvent(event: TransactionDepositFailedEvent){
        updatedAt = event.createdAt
    }
}

enum class TransactionState {
    STARTED,
    FAILED,
    SUCCESS,
}