package com.jantiojo.sendmoney.presentation.home

import java.math.BigDecimal

data class HomeUiState(
    val balance: BigDecimal = BigDecimal("500.00"),
    val isBalanceVisible: Boolean = true
) {
    val formattedBalance: String
        get() = "₱${balance.setScale(2).toPlainString()}"
}
