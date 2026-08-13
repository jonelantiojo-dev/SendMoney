@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.jantiojo.sendmoney.presentation.login

import com.jantiojo.sendmoney.domain.usecase.LoginUseCase
import com.jantiojo.sendmoney.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @Test
    fun `onUsernameChanged updates username and clears error`() {
        viewModel.onUsernameChanged("jonel")

        assertEquals("jonel", viewModel.uiState.value.username)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onPasswordChanged updates password and clears error`() {
        viewModel.onPasswordChanged("secret")

        assertEquals("secret", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `togglePasswordVisibility flips visibility flag`() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)

        viewModel.togglePasswordVisibility()

        assertTrue(viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `successful login clears loading, does not set error, and navigates to home`() = runTest {
        viewModel.onUsernameChanged("test")
        viewModel.onPasswordChanged("123456")
        coEvery { loginUseCase("test", "123456") } returns Result.success(Unit)

        val effects = mutableListOf<LoginEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.login()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(listOf(LoginEffect.NavigateToHome), effects)
        job.cancel()
    }

    @Test
    fun `failed login clears loading and sets error message`() = runTest {
        viewModel.onUsernameChanged("wrong")
        viewModel.onPasswordChanged("wrong")
        coEvery { loginUseCase("wrong", "wrong") } returns
            Result.failure(IllegalArgumentException("Invalid username or password"))

        viewModel.login()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Invalid username or password", viewModel.uiState.value.errorMessage)
    }
}