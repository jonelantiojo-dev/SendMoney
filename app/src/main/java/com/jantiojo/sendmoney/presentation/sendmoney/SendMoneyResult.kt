package com.jantiojo.sendmoney.presentation.sendmoney

import java.math.BigDecimal

sealed interface SendMoneyResult {

    data class Success(
        val amount: BigDecimal,
        val remainingBalance: BigDecimal,
    ) : SendMoneyResult

    data class Error(
        val message: String,
    ) : SendMoneyResult
}
