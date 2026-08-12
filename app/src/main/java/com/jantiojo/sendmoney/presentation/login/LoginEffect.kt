package com.jantiojo.sendmoney.presentation.login

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
}
