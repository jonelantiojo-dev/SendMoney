package com.jantiojo.sendmoney.presentation.sendmoney

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SendMoneyScreenRoute(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SendMoneyViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SendMoneyEffect.NavigateToLogin -> {
                    onLogoutSuccess()
                }
            }
        }
    }

    SendMoneyScreen(
        uiState = state,
        onAmountChanged = viewModel::onAmountChanged,
        onSubmitClick = viewModel::submit,
        onDismissResult = viewModel::dismissResult,
        onBackClick = onBackClick,
        onLogoutClick = viewModel::logout
    )
}

