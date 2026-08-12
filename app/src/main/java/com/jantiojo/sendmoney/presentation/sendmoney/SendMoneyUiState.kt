package com.jantiojo.sendmoney.presentation.sendmoney

import java.math.BigDecimal

data class SendMoneyUiState(
    val amount: String = "",
    val balance: BigDecimal = BigDecimal("500.00"),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val result: SendMoneyResult? = null
) {
    val formattedBalance: String
        get() = "₱${balance.setScale(2).toPlainString()}"

    val isSubmitEnabled: Boolean
        get() = amount.isNotBlank()
}
