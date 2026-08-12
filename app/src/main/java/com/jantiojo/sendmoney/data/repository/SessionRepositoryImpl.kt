package com.jantiojo.sendmoney.data.repository

import com.jantiojo.sendmoney.data.local.SessionLocalDataSource
import com.jantiojo.sendmoney.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource
) : SessionRepository {

    override val isLoggedIn: Flow<Boolean>
        get() = sessionLocalDataSource.isLoggedIn

    override suspend fun saveSession(username: String) {
        sessionLocalDataSource.saveSession(username)
    }

    override suspend fun logout() {
        sessionLocalDataSource.clearSession()
    }
}
