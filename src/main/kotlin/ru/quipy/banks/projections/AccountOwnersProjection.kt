package ru.quipy.banks.projections

import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountOwnersProjection {
    var owners: MutableMap<UUID, Owner> = mutableMapOf()
    var accountOwner: MutableMap<UUID, UUID> = mutableMapOf()

    fun addOwner(ownerId: UUID) {
        owners[ownerId] = Owner(ownerId)
    }

    fun addAccount(ownerId: UUID, accountId: UUID) {
        if (!owners.containsKey(ownerId)) addOwner(ownerId)
        accountOwner[accountId] = ownerId
        owners[ownerId]?.accountCount?.plus(1)
    }

    fun removeAccount(ownerId: UUID, accountId: UUID) {
        accountOwner.remove(accountId)
        owners[ownerId]?.accountCount?.minus(1) ?: throw IllegalArgumentException("Owner:$ownerId not found!")
    }

    fun changeBalance(accountId: UUID, amount: Int) {
        owners[accountOwner[accountId]]?.totalBalance?.plus(amount)
    }

    fun getTotalBalance(ownerId: UUID): Int {
        return owners[ownerId]?.totalBalance ?: throw IllegalArgumentException("Owner:$ownerId not found!")
    }

    fun getAccountCount(ownerId: UUID): Int {
        return owners[ownerId]?.accountCount ?: 0
    }

}

data class Owner(
    val ownerId: UUID,
    var totalBalance: Int = 0,
    var accountCount: Int = 0,
)
