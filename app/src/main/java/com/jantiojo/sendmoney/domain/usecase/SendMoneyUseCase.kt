package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import com.jantiojo.sendmoney.domain.repository.WalletRepository
import java.math.BigDecimal
import javax.inject.Inject

class SendMoneyUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        amount: BigDecimal
    ): Result<Transaction> = runCatching {

        require(amount > BigDecimal.ZERO) {
            "Amount must be greater than zero"
        }

        val balance = walletRepository.getBalance()

        require(amount <= balance) {
            "Insufficient balance"
        }

        val transaction =
            transactionRepository.sendMoney(amount)

        walletRepository.updateBalance(
            balance.subtract(amount)
        )

        transaction
    }
}
