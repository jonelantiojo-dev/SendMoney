package com.jantiojo.sendmoney.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreenRoute(
    onSendMoneyClick: () -> Unit,
    onViewTransactionsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = state,
        onToggleBalanceVisibility = viewModel::toggleBalanceVisibility,
        onSendMoneyClick = onSendMoneyClick,
        onViewTransactionsClick = onViewTransactionsClick,
        onLogoutClick = {},
    )
}
