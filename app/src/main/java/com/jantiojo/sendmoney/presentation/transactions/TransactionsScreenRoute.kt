package com.jantiojo.sendmoney.presentation.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransactionsScreenRoute(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TransactionsEffect.NavigateToLogin -> {
                    onLogoutSuccess()
                }
            }
        }
    }

    TransactionsScreen(
        uiState = state,
        onBackClick = onBackClick,
        onLogoutClick = viewModel::logout
    )
}
