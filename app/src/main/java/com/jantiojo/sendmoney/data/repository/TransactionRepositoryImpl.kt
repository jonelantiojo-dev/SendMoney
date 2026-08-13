package com.jantiojo.sendmoney.data.repository

import com.jantiojo.sendmoney.data.mapper.toDomain
import com.jantiojo.sendmoney.data.remote.api.TransactionApi
import com.jantiojo.sendmoney.data.remote.dto.SendMoneyRequestDto
import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import java.math.BigDecimal
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionApi: TransactionApi,
) : TransactionRepository {

    override suspend fun sendMoney(
        amount: BigDecimal
    ): Transaction {

        val response = transactionApi.postTransaction(
            SendMoneyRequestDto(
                amount = amount.toPlainString()
            )
        )

        return response.toDomain()
    }

    override suspend fun getTransactions(): List<Transaction> {
        return transactionApi
            .getTransactions()
            .map { it.toDomain() }
    }
}

