package com.jantiojo.sendmoney.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendMoneyRequestDto(
    val amount: String,
    val userId: Int = 1,
)
