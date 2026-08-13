package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GetTransactionsUseCaseTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase

    @Before
    fun setUp() {
        transactionRepository = mockk()
        getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)
    }

    @Test
    fun `returns success with transactions from repository`() = runTest {
        val transactions = listOf(
            Transaction(id = "tx-1", amount = BigDecimal("10.00"), createdAt = 1L),
            Transaction(id = "tx-2", amount = BigDecimal("20.00"), createdAt = 2L),
        )
        coEvery { transactionRepository.getTransactions() } returns transactions

        val result = getTransactionsUseCase()

        assertTrue(result.isSuccess)
        assertEquals(transactions, result.getOrNull())
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { transactionRepository.getTransactions() } throws exception

        val result = getTransactionsUseCase()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}