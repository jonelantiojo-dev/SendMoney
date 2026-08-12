package com.jantiojo.sendmoney.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jantiojo.sendmoney.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState =
        _uiState.asStateFlow()

    private val _effect =
        Channel<LoginEffect>()

    val effect =
        _effect.receiveAsFlow()

    fun onUsernameChanged(
        value: String
    ) {
        _uiState.update {
            it.copy(
                username = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(
        value: String
    ) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(
                isPasswordVisible =
                    !it.isPasswordVisible
            )
        }
    }

    fun login() {
        val state = _uiState.value

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            loginUseCase(
                username = state.username,
                password = state.password
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    _effect.send(
                        LoginEffect.NavigateToHome
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                throwable.message
                        )
                    }
                }
        }
    }
}
