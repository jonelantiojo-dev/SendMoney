package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.model.Transaction
import com.jantiojo.sendmoney.domain.repository.TransactionRepository
import com.jantiojo.sendmoney.domain.repository.WalletRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class SendMoneyUseCaseTest {

    private lateinit var walletRepository: WalletRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var sendMoneyUseCase: SendMoneyUseCase

    @Before
    fun setUp() {
        walletRepository = mockk()
        transactionRepository = mockk()
        sendMoneyUseCase = SendMoneyUseCase(walletRepository, transactionRepository)
    }

    @Test
    fun `valid amount within balance succeeds`() = runTest {
        val balance = BigDecimal("500.00")
        val amount = BigDecimal("100.00")
        val transaction = Transaction(id = "tx-1", amount = amount, createdAt = 1L)

        coEvery { walletRepository.getBalance() } returns balance
        coEvery { transactionRepository.sendMoney(amount) } returns transaction
        coEvery { walletRepository.updateBalance(any()) } returns Unit

        val result = sendMoneyUseCase(amount)

        assertTrue(result.isSuccess)
        assertEquals(transaction, result.getOrNull())
    }

    @Test
    fun `zero amount fails without touching repositories`() = runTest {
        val result = sendMoneyUseCase(BigDecimal.ZERO)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { transactionRepository.sendMoney(any()) }
        coVerify(exactly = 0) { walletRepository.updateBalance(any()) }
    }

    @Test
    fun `negative amount fails without touching repositories`() = runTest {
        val result = sendMoneyUseCase(BigDecimal("-50.00"))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { transactionRepository.sendMoney(any()) }
        coVerify(exactly = 0) { walletRepository.updateBalance(any()) }
    }

    @Test
    fun `amount greater than balance fails without sending money`() = runTest {
        coEvery { walletRepository.getBalance() } returns BigDecimal("100.00")

        val result = sendMoneyUseCase(BigDecimal("150.00"))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { transactionRepository.sendMoney(any()) }
        coVerify(exactly = 0) { walletRepository.updateBalance(any()) }
    }

    @Test
    fun `amount equal to balance succeeds`() = runTest {
        val balance = BigDecimal("100.00")
        val transaction = Transaction(id = "tx-2", amount = balance, createdAt = 2L)

        coEvery { walletRepository.getBalance() } returns balance
        coEvery { transactionRepository.sendMoney(balance) } returns transaction
        coEvery { walletRepository.updateBalance(any()) } returns Unit

        val result = sendMoneyUseCase(balance)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { walletRepository.updateBalance(BigDecimal("0.00")) }
    }

    @Test
    fun `repository failure when sending money returns failure and balance is not updated`() = runTest {
        val balance = BigDecimal("500.00")
        val amount = BigDecimal("100.00")
        val exception = RuntimeException("Network error")

        coEvery { walletRepository.getBalance() } returns balance
        coEvery { transactionRepository.sendMoney(amount) } throws exception

        val result = sendMoneyUseCase(amount)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { walletRepository.updateBalance(any()) }
    }

    @Test
    fun `successful transaction updates wallet with correct remaining balance`() = runTest {
        val balance = BigDecimal("500.00")
        val amount = BigDecimal("125.50")
        val transaction = Transaction(id = "tx-3", amount = amount, createdAt = 3L)

        coEvery { walletRepository.getBalance() } returns balance
        coEvery { transactionRepository.sendMoney(amount) } returns transaction
        coEvery { walletRepository.updateBalance(any()) } returns Unit

        sendMoneyUseCase(amount)

        coVerify(exactly = 1) { walletRepository.updateBalance(BigDecimal("374.50")) }
    }
}