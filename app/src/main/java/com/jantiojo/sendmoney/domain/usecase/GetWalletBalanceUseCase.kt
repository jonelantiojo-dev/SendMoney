package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class GetWalletBalanceUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return walletRepository.balance
    }
}
