package com.jantiojo.sendmoney.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun LoginScreenRoute(modifier: Modifier = Modifier) {
    LoginScreen(
        state = LoginUiState(
            username = "jonel",
            password = "123456",
            isPasswordVisible = true
        ),
        onUserNameChanged = {},
        onPasswordChanged = {},
        onPasswordVisibilityChanged = {},
        onSignInClicked = {},
        modifier = modifier
    )
}