package com.jantiojo.sendmoney.presentation.transactions

sealed interface TransactionsEffect {
    data object NavigateToLogin : TransactionsEffect
}
