package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk(relaxed = true)
        loginUseCase = LoginUseCase(sessionRepository)
    }

    @Test
    fun `blank username returns failure and does not save session`() = runTest {
        val result = loginUseCase(username = "", password = "123456")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { sessionRepository.saveSession(any()) }
    }

    @Test
    fun `blank password returns failure and does not save session`() = runTest {
        val result = loginUseCase(username = "test", password = "")

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { sessionRepository.saveSession(any()) }
    }

    @Test
    fun `invalid credentials returns failure and does not save session`() = runTest {
        val result = loginUseCase(username = "wrong", password = "wrong")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { sessionRepository.saveSession(any()) }
    }

    @Test
    fun `valid credentials saves session and returns success`() = runTest {
        coEvery { sessionRepository.saveSession("test") } returns Unit

        val result = loginUseCase(username = "test", password = "123456")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { sessionRepository.saveSession("test") }
    }
}