package com.jantiojo.sendmoney.data.mapper

import com.jantiojo.sendmoney.data.remote.dto.TransactionDto
import com.jantiojo.sendmoney.domain.model.Transaction

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id.toString(),
        amount = amount.toBigDecimal(),
        createdAt = System.currentTimeMillis(),
    )
}
