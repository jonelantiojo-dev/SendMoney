@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.jantiojo.sendmoney.presentation.sendmoney

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.usecase.GetWalletBalanceUseCase
import com.jantiojo.sendmoney.domain.usecase.LogoutUseCase
import com.jantiojo.sendmoney.domain.usecase.SendMoneyUseCase
import com.jantiojo.sendmoney.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
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
import java.math.BigDecimal

class SendMoneyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sendMoneyUseCase: SendMoneyUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var getWalletBalanceUseCase: GetWalletBalanceUseCase
    private lateinit var balanceFlow: MutableStateFlow<BigDecimal>

    private fun createViewModel(): SendMoneyViewModel {
        return SendMoneyViewModel(
            sendMoneyUseCase = sendMoneyUseCase,
            logoutUseCase = logoutUseCase,
            getWalletBalanceUseCase = getWalletBalanceUseCase,
        )
    }

    @Before
    fun setUp() {
        sendMoneyUseCase = mockk()
        logoutUseCase = mockk(relaxed = true)
        balanceFlow = MutableStateFlow(BigDecimal("500.00"))
        getWalletBalanceUseCase = mockk()
        every { getWalletBalanceUseCase() } returns balanceFlow
    }

    @Test
    fun `init collects wallet balance into ui state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(BigDecimal("500.00"), viewModel.uiState.value.balance)
    }

    @Test
    fun `onAmountChanged updates amount and clears error`() {
        val viewModel = createViewModel()

        viewModel.onAmountChanged("100")

        assertEquals("100", viewModel.uiState.value.amount)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `submit with blank amount does nothing`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.submit()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.result)
        coVerify(exactly = 0) { sendMoneyUseCase(any()) }
    }

    @Test
    fun `submit with zero amount surfaces error result`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("0")
        coEvery { sendMoneyUseCase(BigDecimal("0")) } returns
            Result.failure(IllegalArgumentException("Amount must be greater than zero"))

        viewModel.submit()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        val result = viewModel.uiState.value.result
        assertTrue(result is SendMoneyResult.Error)
        assertEquals(
            "Amount must be greater than zero",
            (result as SendMoneyResult.Error).message
        )
    }

    @Test
    fun `submit with negative amount surfaces error result`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("-50")
        coEvery { sendMoneyUseCase(BigDecimal("-50")) } returns
            Result.failure(IllegalArgumentException("Amount must be greater than zero"))

        viewModel.submit()
        advanceUntilIdle()

        val result = viewModel.uiState.value.result
        assertTrue(result is SendMoneyResult.Error)
        assertEquals(
            "Amount must be greater than zero",
            (result as SendMoneyResult.Error).message
        )
    }

    @Test
    fun `submit with amount greater than balance surfaces error result`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("1000")
        coEvery { sendMoneyUseCase(BigDecimal("1000")) } returns
            Result.failure(IllegalArgumentException("Insufficient balance"))

        viewModel.submit()
        advanceUntilIdle()

        val result = viewModel.uiState.value.result
        assertTrue(result is SendMoneyResult.Error)
        assertEquals("Insufficient balance", (result as SendMoneyResult.Error).message)
    }

    @Test
    fun `successful submit clears amount and sets success result with correct remaining balance`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.onAmountChanged("100")
            val transaction = Transaction(id = "tx-1", amount = BigDecimal("100"), createdAt = 1L)
            coEvery { sendMoneyUseCase(BigDecimal("100")) } returns Result.success(transaction)

            // The wallet balance flow (backed by the repository) has already emitted the
            // post-transaction balance by the time the use case call completes.
            balanceFlow.value = BigDecimal("400.00")
            advanceUntilIdle()

            viewModel.submit()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("", state.amount)
            val result = state.result
            assertTrue(result is SendMoneyResult.Success)
            result as SendMoneyResult.Success
            assertEquals(BigDecimal("100"), result.amount)
            assertEquals(BigDecimal("400.00"), result.remainingBalance)
        }

    @Test
    fun `repository failure during submit surfaces error result and keeps amount`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("100")
        coEvery { sendMoneyUseCase(BigDecimal("100")) } returns
            Result.failure(RuntimeException("Network error"))

        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("100", state.amount)
        val result = state.result
        assertTrue(result is SendMoneyResult.Error)
        assertEquals("Network error", (result as SendMoneyResult.Error).message)
    }

    @Test
    fun `repository failure with no message falls back to default error message`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("100")
        coEvery { sendMoneyUseCase(BigDecimal("100")) } returns
            Result.failure(RuntimeException())

        viewModel.submit()
        advanceUntilIdle()

        val result = viewModel.uiState.value.result
        assertTrue(result is SendMoneyResult.Error)
        assertEquals("Unable to send money", (result as SendMoneyResult.Error).message)
    }

    @Test
    fun `dismissResult clears result`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAmountChanged("100")
        coEvery { sendMoneyUseCase(BigDecimal("100")) } returns
            Result.failure(RuntimeException("boom"))
        viewModel.submit()
        advanceUntilIdle()

        viewModel.dismissResult()

        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun `logout invokes use case and emits navigate to login effect`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<SendMoneyEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { logoutUseCase() }
        assertEquals(listOf(SendMoneyEffect.NavigateToLogin), effects)
        job.cancel()
    }
}
