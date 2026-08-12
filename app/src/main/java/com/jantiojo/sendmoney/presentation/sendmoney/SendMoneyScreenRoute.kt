package com.jantiojo.sendmoney.presentation.sendmoney

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SendMoneyScreenRoute(modifier: Modifier = Modifier) {
    SendMoneyScreen(
        uiState = SendMoneyUiState(
            amount = "100"
        )
    )
}
