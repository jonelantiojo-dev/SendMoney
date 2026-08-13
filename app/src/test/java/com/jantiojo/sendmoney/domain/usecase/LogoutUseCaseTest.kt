package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.repository.SessionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk(relaxed = true)
        logoutUseCase = LogoutUseCase(sessionRepository)
    }

    @Test
    fun `invoking logout use case logs out the session`() = runTest {
        logoutUseCase()

        coVerify(exactly = 1) { sessionRepository.logout() }
    }
}