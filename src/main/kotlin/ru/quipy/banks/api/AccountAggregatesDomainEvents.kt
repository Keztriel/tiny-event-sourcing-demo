package ru.quipy.banks.api

import ru.quipy.banks.logic.TransactionState
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val ACCOUNT_CREATED_EVENT = "ACCOUNT_CREATED_EVENT"
const val ACCOUNT_REMOVED_EVENT = "ACCOUNT_REMOVED_EVENT"
const val BALANCE_CHANGED_EVENT = "BALANCE_CHANGED_EVENT"
const val TRANSACTION_WITHDRAW_EVENT = "TRANSACTION_WITHDRAW_EVENT"
const val TRANSACTION_DEPOSIT_EVENT = "TRANSACTION_DEPOSIT_EVENT"
const val TRANSACTION_STARTED_EVENT = "TRANSACTION_STARTED_EVENT"
const val TRANSACTION_DEPOSIT_FAILED_EVENT = "TRANSACTION_DEPOSIT_FAILED_EVENT"
const val TRANSACTION_WITHDRAW_FAILED_EVENT = "TRANSACTION_WITHDRAW_FAILED_EVENT"

@DomainEvent(name = ACCOUNT_CREATED_EVENT)
class AccountCreatedEvent(
    val accountId: UUID,
    val ownerId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = ACCOUNT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = ACCOUNT_REMOVED_EVENT)
class AccountRemovedEvent(
    val accountId: UUID,
    val ownerId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = ACCOUNT_REMOVED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = BALANCE_CHANGED_EVENT)
class BalanceChangedEvent(
    val accountId: UUID,
    val amount: Int,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = BALANCE_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TRANSACTION_STARTED_EVENT)
class TransactionStartedEvent(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = TRANSACTION_STARTED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TRANSACTION_DEPOSIT_EVENT)
class TransactionDepositEvent(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = TRANSACTION_DEPOSIT_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TRANSACTION_WITHDRAW_EVENT)
class TransactionWithdrawEvent(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = TRANSACTION_WITHDRAW_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TRANSACTION_DEPOSIT_FAILED_EVENT)
class TransactionDepositFailedEvent(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = TRANSACTION_DEPOSIT_FAILED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TRANSACTION_WITHDRAW_FAILED_EVENT)
class TransactionWithdrawFailedEvent(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = TRANSACTION_WITHDRAW_FAILED_EVENT,
    createdAt = createdAt,
)