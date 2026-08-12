package com.jantiojo.sendmoney.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jantiojo.sendmoney.presentation.navigation.AppNavigation

@Composable
fun SendMoneyApp(
    viewModel: AppViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    AppNavigation(
        isLoggedIn = state.isLoggedIn
    )
}
