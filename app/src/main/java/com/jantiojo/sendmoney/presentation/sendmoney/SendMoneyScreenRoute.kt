package com.jantiojo.sendmoney.presentation.sendmoney

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SendMoneyScreenRoute(
    onBackClick: () -> Unit,
    viewModel: SendMoneyViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SendMoneyScreen(
        uiState = state,
        onAmountChanged = viewModel::onAmountChanged,
        onSubmitClick = viewModel::submit,
        onDismissResult = viewModel::dismissResult,
        onBackClick = onBackClick
    )
}

