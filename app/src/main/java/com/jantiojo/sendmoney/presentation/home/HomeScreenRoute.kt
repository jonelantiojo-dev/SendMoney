package com.jantiojo.sendmoney.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreenRoute(
    onSendMoneyClick: () -> Unit,
    onViewTransactionsClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToLogin -> {
                    onLogoutSuccess()
                }
            }
        }
    }

    HomeScreen(
        uiState = state,
        onToggleBalanceVisibility = viewModel::toggleBalanceVisibility,
        onSendMoneyClick = onSendMoneyClick,
        onViewTransactionsClick = onViewTransactionsClick,
        onLogoutClick = viewModel::logout,
    )
}
