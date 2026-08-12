package com.jantiojo.sendmoney.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank()
}
