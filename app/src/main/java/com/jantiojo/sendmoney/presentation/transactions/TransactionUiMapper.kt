package com.jantiojo.sendmoney.presentation.transactions

import com.jantiojo.sendmoney.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Transaction.toUiModel(): TransactionUiModel {
    return TransactionUiModel(
        id = id,
        amount = amount,
        date = formatTransactionDate(createdAt)
    )
}

private fun formatTransactionDate(
    timestamp: Long
): String {
    val formatter = SimpleDateFormat(
        "MMM dd, yyyy • hh:mm a",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}
