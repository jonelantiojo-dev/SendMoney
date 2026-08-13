@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.jantiojo.sendmoney.presentation.transactions

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.usecase.GetTransactionsUseCase
import com.jantiojo.sendmoney.domain.usecase.LogoutUseCase
import com.jantiojo.sendmoney.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class TransactionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        getTransactionsUseCase = mockk()
        logoutUseCase = mockk(relaxed = true)
    }

    @Test
    fun `init loads transactions and maps them to ui models`() = runTest {
        val transactions = listOf(
            Transaction(id = "tx-1", amount = BigDecimal("10.00"), createdAt = 1_700_000_000_000L),
            Transaction(id = "tx-2", amount = BigDecimal("20.00"), createdAt = 1_700_000_100_000L),
        )
        coEvery { getTransactionsUseCase() } returns Result.success(transactions)

        val viewModel = TransactionsViewModel(getTransactionsUseCase, logoutUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(2, state.transactions.size)
        assertEquals("tx-1", state.transactions[0].id)
        assertEquals(BigDecimal("10.00"), state.transactions[0].amount)
        assertEquals(transactions[0].toUiModel().date, state.transactions[0].date)
    }

    @Test
    fun `init failure sets error message and empty list`() = runTest {
        coEvery { getTransactionsUseCase() } returns Result.failure(RuntimeException("Network error"))

        val viewModel = TransactionsViewModel(getTransactionsUseCase, logoutUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Network error", state.errorMessage)
        assertEquals(emptyList<Any>(), state.transactions)
    }

    @Test
    fun `failure with no message falls back to default error message`() = runTest {
        coEvery { getTransactionsUseCase() } returns Result.failure(RuntimeException())

        val viewModel = TransactionsViewModel(getTransactionsUseCase, logoutUseCase)
        advanceUntilIdle()

        assertEquals("Unable to load transactions", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadTransactions can be retried after a failure`() = runTest {
        coEvery { getTransactionsUseCase() } returns Result.failure(RuntimeException("Network error"))
        val viewModel = TransactionsViewModel(getTransactionsUseCase, logoutUseCase)
        advanceUntilIdle()

        val transactions = listOf(
            Transaction(id = "tx-1", amount = BigDecimal("10.00"), createdAt = 1L)
        )
        coEvery { getTransactionsUseCase() } returns Result.success(transactions)

        viewModel.loadTransactions()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals(1, state.transactions.size)
    }

    @Test
    fun `logout invokes use case and emits navigate to login effect`() = runTest {
        coEvery { getTransactionsUseCase() } returns Result.success(emptyList())
        val viewModel = TransactionsViewModel(getTransactionsUseCase, logoutUseCase)
        advanceUntilIdle()

        val effects = mutableListOf<TransactionsEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { logoutUseCase() }
        assertEquals(listOf(TransactionsEffect.NavigateToLogin), effects)
        job.cancel()
    }
}