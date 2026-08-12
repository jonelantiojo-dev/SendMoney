package com.jantiojo.sendmoney.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    val isLoggedIn: Flow<Boolean>

    suspend fun saveSession(username: String)

    suspend fun logout()
}
