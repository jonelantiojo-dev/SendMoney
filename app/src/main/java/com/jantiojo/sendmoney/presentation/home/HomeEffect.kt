package com.jantiojo.sendmoney.presentation.home

sealed interface HomeEffect {
    data object NavigateToLogin : HomeEffect
}
