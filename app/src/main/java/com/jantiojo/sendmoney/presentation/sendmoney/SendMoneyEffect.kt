package com.jantiojo.sendmoney.presentation.sendmoney

sealed interface SendMoneyEffect {
    data object NavigateToLogin : SendMoneyEffect
}
