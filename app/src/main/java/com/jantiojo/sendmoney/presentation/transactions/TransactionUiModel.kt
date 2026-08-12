package com.jantiojo.sendmoney.presentation.transactions

import java.math.BigDecimal

data class TransactionUiModel(
    val id: String,
    val amount: BigDecimal,
    val date: String,
)
