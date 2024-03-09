package ru.quipy.banks.logic

import ru.quipy.banks.api.*
import ru.quipy.banks.config.Services
import ru.quipy.domain.Event
import java.util.*

const val ACCOUNT_COUNT_LIMIT = 5
const val ACCOUNT_BALANCE_LIMIT = 10_000
const val TOTAL_BALANCE_LIMIT = 25_000

fun AccountAggregateState.create(
    id: UUID, ownerId: UUID,
    services: Services
) : AccountCreatedEvent {
    val accountCount = services.accountOwnersProjection.getAccountCount(ownerId)

    if (accountCount > ACCOUNT_COUNT_LIMIT)
        throw IllegalArgumentException("Can't create new account. User's(${ownerId}) account limit(${ACCOUNT_COUNT_LIMIT}) is already reached!")

    services.accountOwnersProjection.addAccount(ownerId, id)
    return AccountCreatedEvent(accountId = id, ownerId = ownerId)
}

fun AccountAggregateState.remove(
    id: UUID, ownerId: UUID,
    services: Services,
) : AccountRemovedEvent {
    val account = services.accountEsService.getState(id)
        ?: throw IllegalArgumentException("Account:$id not found")

    if (account.balance != 0){
        throw java.lang.IllegalStateException("Account:$id balance:${account.balance}. Cannot delete account with non-zero balance!")
    }

    services.accountOwnersProjection.removeAccount(ownerId, id)
    return AccountRemovedEvent(accountId = id, ownerId = ownerId)
}

fun AccountAggregateState.changeBalance(
    id: UUID, amount: Int,
    services: Services,
) : BalanceChangedEvent {

    val account = services.accountEsService.getState(id) ?:
        throw IllegalArgumentException("Account:${id} not found!")

    if (account.removed) throw IllegalStateException("Account $id is terminated!")

    if (account.balance + amount < 0){
        throw IllegalArgumentException("Account $id doesn't have enough money")
    }
    if (account.balance + amount > ACCOUNT_BALANCE_LIMIT){
        throw IllegalArgumentException("Account $id can't have more then $ACCOUNT_BALANCE_LIMIT")
    }
    if (services.accountOwnersProjection.getTotalBalance(ownerId) + amount > TOTAL_BALANCE_LIMIT){
        throw IllegalArgumentException("User $ownerId can't have more then $TOTAL_BALANCE_LIMIT")
    }

    return BalanceChangedEvent(accountId = id, amount = amount)
}

fun AccountAggregateState.startTransaction(
    id: UUID, accountFromId: UUID, accountToId: UUID, amount: Int,
    services: Services,
) : TransactionStartedEvent {

    val accountFrom = services.accountEsService.getState(accountFromId)
        ?: throw IllegalArgumentException("No account with id $accountFromId")
    val accountTo = services.accountEsService.getState(accountToId)
        ?: throw IllegalArgumentException("No account with id $accountToId")

    if (accountFrom.removed) throw IllegalStateException("Account $accountFromId is terminated!")
    if (accountTo.removed) throw IllegalStateException("Account $accountToId is terminated!")

    return TransactionStartedEvent(
        transactionId = id,
        accountFromId = accountFromId,
        accountToId = accountToId,
        amount = amount,
        status = TransactionState.STARTED
    )
}

fun AccountAggregateState.withdraw(
    id: UUID, accountFromId: UUID, accountToId: UUID, amount: Int,
    services: Services,
) : Event<AccountAggregate> {

    val accountFrom = services.accountEsService.getState(accountFromId)
        ?: throw IllegalArgumentException("No account with id $accountFromId")

    if (accountFrom.removed || accountFrom.balance + amount < 0) {
        return TransactionWithdrawFailedEvent(
            transactionId = id,
            accountFromId = accountFromId,
            accountToId = accountToId,
            amount = amount,
            status = TransactionState.FAILED,
        )
    }

    return TransactionWithdrawEvent(
        transactionId = id,
        accountFromId = accountFromId,
        accountToId = accountToId,
        amount = amount,
        status = TransactionState.SUCCESS,
    )
}

fun AccountAggregateState.deposit(
    id: UUID, accountFromId: UUID, accountToId: UUID, amount: Int,
    services: Services,
) : Event<AccountAggregate> {

    val accountTo = services.accountEsService.getState(accountToId)
        ?: throw IllegalArgumentException("No account with id $accountToId")

    if (accountTo.removed || accountTo.balance + amount > ACCOUNT_BALANCE_LIMIT || services.accountOwnersProjection.getTotalBalance(accountTo.ownerId) + amount > TOTAL_BALANCE_LIMIT) {
        return TransactionDepositFailedEvent(
            transactionId = id,
            accountFromId = accountFromId,
            accountToId = accountToId,
            amount = amount,
            status = TransactionState.FAILED,
        )
    }

    return TransactionDepositEvent(
        transactionId = id,
        accountFromId = accountFromId,
        accountToId = accountToId,
        amount = amount,
        status = TransactionState.SUCCESS,
    )
}