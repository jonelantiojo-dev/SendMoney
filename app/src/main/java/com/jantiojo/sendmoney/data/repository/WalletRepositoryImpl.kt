package com.jantiojo.sendmoney.data.repository

import com.jantiojo.sendmoney.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor() : WalletRepository {

    private val _balance =
        MutableStateFlow(BigDecimal("500.00"))

    override val balance: Flow<BigDecimal>
        get() = _balance

    override suspend fun getBalance(): BigDecimal {
        return _balance.value
    }

    override suspend fun updateBalance(newBalance: BigDecimal) {
        _balance.value = newBalance
    }
}
