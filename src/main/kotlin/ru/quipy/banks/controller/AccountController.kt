package ru.quipy.banks.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.banks.api.AccountCreatedEvent
import ru.quipy.banks.api.AccountRemovedEvent
import ru.quipy.banks.api.BalanceChangedEvent
import ru.quipy.banks.api.TransactionStartedEvent
import ru.quipy.banks.config.Services
import ru.quipy.banks.logic.changeBalance
import ru.quipy.banks.logic.create
import ru.quipy.banks.logic.remove
import ru.quipy.banks.logic.startTransaction
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    val services: Services
) {

    @PostMapping("/add")
    fun createAccount(@RequestParam ownerId: UUID, @RequestParam accountId: UUID = UUID.randomUUID()) : AccountCreatedEvent {
        return services.accountEsService.create { it.create(accountId, ownerId, services) }
    }

    @PostMapping("/{accountId}/remove")
    fun removeAccount(@PathVariable accountId: UUID, @RequestParam ownerId: UUID) : AccountRemovedEvent {
        return services.accountEsService.update(accountId) { it.remove(accountId, ownerId, services) }
    }

    @PostMapping("/{accountId}/balance")
    fun changeBalance(@PathVariable accountId: UUID, @RequestParam amount: Int) : BalanceChangedEvent {
        return services.accountEsService.update(accountId) {
            it.changeBalance(accountId, amount, services)
        }
    }

    @PostMapping("/{accountFromId}/transfer/{accountToId}")
    fun transfer(@PathVariable accountFromId: UUID, @PathVariable accountToId: UUID, @RequestParam amount: Int) : TransactionStartedEvent {
        return services.accountEsService.update(accountFromId) {
            it.startTransaction(UUID.randomUUID(), accountFromId, accountToId, amount, services)
        }
    }


}