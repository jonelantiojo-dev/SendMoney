package com.jantiojo.sendmoney.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jantiojo.sendmoney.domain.usecase.GetTransactionsUseCase
import com.jantiojo.sendmoney.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(TransactionsUiState())

    val uiState = _uiState.asStateFlow()

    private val _effect =
        Channel<TransactionsEffect>()

    val effect = _effect.receiveAsFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getTransactionsUseCase()
                .onSuccess { transactions ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            transactions = transactions.map {
                                    transaction ->
                                transaction.toUiModel()
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                error.message
                                    ?: "Unable to load transactions"
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _effect.send(
                TransactionsEffect.NavigateToLogin
            )
        }
    }

}