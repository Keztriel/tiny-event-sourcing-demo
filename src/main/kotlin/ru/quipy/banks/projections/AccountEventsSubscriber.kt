package ru.quipy.banks.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.banks.api.*
import ru.quipy.banks.config.Services
import ru.quipy.banks.logic.changeBalance
import ru.quipy.banks.logic.deposit
import ru.quipy.banks.logic.withdraw
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class AccountEventsSubscriber {
    var logger: Logger = LoggerFactory.getLogger(AccountEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    lateinit var services: Services

    @PostConstruct
    fun init() {

        subscriptionsManager.createSubscriber(AccountAggregate::class, "account-event-listener") {

            `when` (AccountCreatedEvent::class) { event ->

                logger.info("Account:${event.accountId} has been created, owner:${event.ownerId}!")
                services.accountOwnersProjection.addAccount(ownerId = event.ownerId, accountId = event.accountId)
            }

            `when` (BalanceChangedEvent::class) { event ->

                services.accountOwnersProjection.changeBalance(event.accountId, event.amount)
            }

            `when` (TransactionStartedEvent::class) { event ->
                logger.info("Transaction:${event.transactionId}:${event.status.name} transfer ${event.amount} from ${event.accountFromId} to ${event.accountToId}!")

                services.accountEsService.update(event.accountFromId) {
                    it.withdraw(event.transactionId, event.accountFromId, event.accountToId, event.amount, services)
                }

                services.transactionProjection.addTransaction(
                    transactionId = event.transactionId,
                    accountFromId = event.accountFromId,
                    accountToId = event.accountToId,
                    amount = event.amount,
                    status = event.status,
                )
            }

            `when` (TransactionDepositEvent::class) { event ->
                logger.info("Transaction:${event.transactionId}:${event.status.name} deposit ${event.amount} to ${event.accountToId}!")

                services.transactionProjection.addTransaction(
                    transactionId = event.transactionId,
                    accountFromId = event.accountFromId,
                    accountToId = event.accountToId,
                    amount = event.amount,
                    status = event.status,
                )
            }

            `when` (TransactionWithdrawEvent::class) { event ->
                logger.info("Transaction:${event.transactionId}:${event.status.name} withdraw ${event.amount} from ${event.accountFromId}!")

                services.accountEsService.update(event.accountToId) {
                    it.deposit(event.transactionId, event.accountFromId, event.accountToId, event.amount, services)
                }

                services.transactionProjection.addTransaction(
                    transactionId = event.transactionId,
                    accountFromId = event.accountFromId,
                    accountToId = event.accountToId,
                    amount = event.amount,
                    status = event.status,
                )
            }

            `when` (TransactionDepositFailedEvent::class) { event ->
                logger.info("Transaction:${event.transactionId} DEPOSIT FAILED!")

                services.transactionProjection.addTransaction(
                    transactionId = event.transactionId,
                    accountFromId = event.accountFromId,
                    accountToId = event.accountToId,
                    amount = event.amount,
                    status = event.status,
                )
                services.accountEsService.update(event.accountFromId) {
                    it.changeBalance(event.accountFromId, event.amount, services)
                }
            }

            `when` (TransactionWithdrawFailedEvent::class) { event ->
                logger.info("Transaction:${event.transactionId} WITHDRAW FAILED!")

                services.transactionProjection.addTransaction(
                    transactionId = event.transactionId,
                    accountFromId = event.accountFromId,
                    accountToId = event.accountToId,
                    amount = event.amount,
                    status = event.status,
                )
            }
        }
    }
}