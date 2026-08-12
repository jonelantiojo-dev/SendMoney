package com.jantiojo.sendmoney.domain.repository

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface WalletRepository {
    val balance: Flow<BigDecimal>

    suspend fun updateBalance(
        newBalance: BigDecimal
    )
}
