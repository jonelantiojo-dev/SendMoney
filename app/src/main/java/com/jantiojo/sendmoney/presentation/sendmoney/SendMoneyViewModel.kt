package com.jantiojo.sendmoney.presentation.sendmoney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jantiojo.sendmoney.domain.usecase.GetWalletBalanceUseCase
import com.jantiojo.sendmoney.domain.usecase.LogoutUseCase
import com.jantiojo.sendmoney.domain.usecase.SendMoneyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val sendMoneyUseCase: SendMoneyUseCase,
    private val logoutUseCase: LogoutUseCase,
    getWalletBalanceUseCase: GetWalletBalanceUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(SendMoneyUiState())

    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<SendMoneyEffect>()

    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            getWalletBalanceUseCase()
                .collect { balance ->
                    _uiState.update {
                        it.copy(balance = balance)
                    }
                }
        }
    }

    fun onAmountChanged(value: String) {

        _uiState.update {
            it.copy(
                amount = value,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val amount =
            _uiState.value.amount.toBigDecimalOrNull()
                ?: return

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            sendMoneyUseCase(amount)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            amount = "",
                            result = SendMoneyResult.Success(
                                amount = amount,
                                remainingBalance = _uiState.value.balance
                            )
                        )
                    }

                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            result = SendMoneyResult.Error(
                                message = error.message
                                    ?: "Unable to send money"
                            )
                        )
                    }
                }
        }
    }

    fun dismissResult() {
        _uiState.update {
            it.copy(result = null)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _effect.send(
                SendMoneyEffect.NavigateToLogin
            )
        }
    }
}
