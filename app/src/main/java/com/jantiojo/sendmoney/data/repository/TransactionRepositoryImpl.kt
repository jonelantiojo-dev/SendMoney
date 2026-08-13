package com.jantiojo.sendmoney.data.repository

import com.jantiojo.sendmoney.data.local.TransactionLocalDataSource
import com.jantiojo.sendmoney.data.mapper.toDomain
import com.jantiojo.sendmoney.data.remote.api.TransactionApi
import com.jantiojo.sendmoney.data.remote.dto.SendMoneyRequestDto
import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import java.math.BigDecimal
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionApi: TransactionApi,
    private val localDataSource: TransactionLocalDataSource
) : TransactionRepository {

    override suspend fun sendMoney(
        amount: BigDecimal
    ): Transaction {

        val response = transactionApi.postTransaction(
            SendMoneyRequestDto(
                amount = amount.toPlainString()
            )
        )
        val transaction = response.toDomain()

        localDataSource.saveTransaction(transaction)

        return response.toDomain()
    }

    override suspend fun getTransactions(): List<Transaction> {

        //it useless to call as it has no amount in the response
//        transactionApi
//            .getTransactions()
//        {
//            "userId": 1,
//            "id": 1,
//            "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
//            "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
//        }

        return localDataSource.getTransactions()
    }
}

