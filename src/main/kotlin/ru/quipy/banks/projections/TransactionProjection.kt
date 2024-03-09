package ru.quipy.banks.projections

import org.springframework.stereotype.Service
import ru.quipy.banks.logic.TransactionState
import java.util.UUID

@Service
class TransactionProjection {
    var transactions: MutableMap<UUID, Transaction> = mutableMapOf()

    fun addTransaction(transactionId: UUID,accountFromId: UUID, accountToId: UUID, amount: Int, status: TransactionState) {
        transactions[transactionId] = Transaction(transactionId,accountFromId, accountToId, amount, status)
    }
}

data class Transaction(
    val transactionId: UUID,
    val accountFromId: UUID,
    val accountToId: UUID,
    val amount: Int,
    val status: TransactionState
)
