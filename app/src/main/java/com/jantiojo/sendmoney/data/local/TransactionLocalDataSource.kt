package com.jantiojo.sendmoney.data.local

import com.jantiojo.sendmoney.domain.model.Transaction
import javax.inject.Inject

class TransactionLocalDataSource @Inject constructor() {

    private val transactions = mutableListOf<Transaction>()

    fun saveTransaction(
        transaction: Transaction
    ) {
        transactions.add(
            index = 0,
            element = transaction
        )
    }

    fun getTransactions(): List<Transaction> {
        return transactions.toList()
    }
}
