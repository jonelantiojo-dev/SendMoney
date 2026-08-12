package com.jantiojo.sendmoney.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jantiojo.sendmoney.domain.usecase.GetWalletBalanceUseCase
import com.jantiojo.sendmoney.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getWalletBalanceUseCase: GetWalletBalanceUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _isBalanceVisible =
        MutableStateFlow(true)

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    val uiState: StateFlow<HomeUiState> =
        combine(
            getWalletBalanceUseCase(),
            _isBalanceVisible
        ) { balance, isVisible ->

            HomeUiState(
                balance = balance,
                isBalanceVisible = isVisible
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()

            _effect.send(
                HomeEffect.NavigateToLogin
            )
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.update { !it }
    }
}
