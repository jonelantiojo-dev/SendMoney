package com.jantiojo.sendmoney.presentation.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jantiojo.sendmoney.presentation.login.LoginScreen
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactions: List<TransactionUiModel>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Transactions")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = transactions,
                key = { transaction ->
                    transaction.id
                }
            ) { transaction ->
                TransactionItem(
                    transaction = transaction
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview() {
    TransactionsScreen(
        transactions = listOf(
            TransactionUiModel(
                id = "1",
                amount = BigDecimal("200.00"),
                date = "Aug 12, 2026 • 8:15 AM",
            ),
            TransactionUiModel(
                id = "2",
                amount = BigDecimal("50.00"),
                date = "Aug 11, 2026 • 6:40 PM",
            ),
            TransactionUiModel(
                id = "3",
                amount = BigDecimal("125.50"),
                date = "Aug 10, 2026 • 2:30 PM",
            ),
        )
    )
}
