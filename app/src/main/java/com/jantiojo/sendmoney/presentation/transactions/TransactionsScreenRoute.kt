package com.jantiojo.sendmoney.presentation.transactions

import androidx.compose.runtime.Composable
import java.math.BigDecimal

@Composable
fun TransactionsScreenRoute(
    onBackClick: () -> Unit,
) {
    TransactionsScreen(
        uiState = TransactionsUiState(
            transactions = listOf(
                TransactionUiModel(
                    id = "1",
                    amount = BigDecimal("200.00"),
                    date = "Aug 12, 2026 • 8:15 AM",
                ),
                TransactionUiModel(
                    id = "2",
                    amount = BigDecimal("50.00"),
                    date = "Aug 11, 2026 • 6:40 PM",
                ),
                TransactionUiModel(
                    id = "3",
                    amount = BigDecimal("125.50"),
                    date = "Aug 10, 2026 • 2:30 PM",
                )
            )
        ),
        onBackClick = onBackClick
    )
}