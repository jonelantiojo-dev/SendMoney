package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Result<List<Transaction>> {
        return runCatching {
            transactionRepository.getTransactions()
        }
    }
}
