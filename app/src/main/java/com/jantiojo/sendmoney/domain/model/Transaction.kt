package com.jantiojo.sendmoney.domain.model

import java.math.BigDecimal

data class Transaction(
    val id: String,
    val amount: BigDecimal,
    val createdAt: Long
)
