package com.jantiojo.sendmoney.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.math.BigDecimal

@Composable
fun HomeScreenRoute(modifier: Modifier = Modifier) {
    HomeScreen(
        uiState = HomeUiState(
            balance = BigDecimal("500.00"),
            isBalanceVisible = true,
        ),
        onToggleBalanceVisibility = {},
        onSendMoneyClick = {},
        onViewTransactionsClick = {},
        onLogoutClick = {},
    )
}
