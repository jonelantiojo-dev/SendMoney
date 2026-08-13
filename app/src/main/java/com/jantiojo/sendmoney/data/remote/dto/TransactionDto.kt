package com.jantiojo.sendmoney.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: Int,
    val userId: Int? = null,
    val amount: String,
)
