package com.jantiojo.sendmoney.presentation.transactions

data class TransactionsUiState(
    val transactions: List<TransactionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
