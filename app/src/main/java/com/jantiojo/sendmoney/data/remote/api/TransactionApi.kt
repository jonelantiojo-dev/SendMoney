package com.jantiojo.sendmoney.data.remote.api

import com.jantiojo.sendmoney.data.remote.dto.SendMoneyRequestDto
import com.jantiojo.sendmoney.data.remote.dto.TransactionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TransactionApi {

    @POST("posts")
    suspend fun postTransaction(
        @Body request: SendMoneyRequestDto
    ): TransactionDto

    @GET("posts")
    suspend fun getTransactions(): List<TransactionDto>
}
