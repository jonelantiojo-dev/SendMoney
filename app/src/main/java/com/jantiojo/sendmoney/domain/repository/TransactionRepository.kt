package com.jantiojo.sendmoney.domain.repository

import com.jantiojo.sendmoney.domain.model.Transaction
import java.math.BigDecimal

interface TransactionRepository {
    suspend fun sendMoney(
        amount: BigDecimal
    ): Transaction

    suspend fun getTransactions(): List<Transaction>
}
