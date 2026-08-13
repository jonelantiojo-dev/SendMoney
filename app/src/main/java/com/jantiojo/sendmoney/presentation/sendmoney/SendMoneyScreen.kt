package com.jantiojo.sendmoney.presentation.sendmoney

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jantiojo.sendmoney.presentation.ui.theme.SendMoneyTheme
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    uiState: SendMoneyUiState,
    onAmountChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onDismissResult: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Send Money")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
        ) {

            Text(
                text = "Available Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = uiState.formattedBalance,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = onAmountChanged,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Amount")
                },
                prefix = {
                    Text("₱")
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = true,
            ) {
                Text("Submit")
            }
        }
    }

    uiState.result?.let { result ->
        ModalBottomSheet(
            onDismissRequest = onDismissResult
        ) {
            SendMoneyResultContent(
                result = result,
                onDoneClick = onDismissResult
            )
        }
    }

}

@Composable
private fun SendMoneyResultContent(
    result: SendMoneyResult,
    onDoneClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        when (result) {

            is SendMoneyResult.Success -> {

                Text(
                    text = "Money Sent",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "₱${result.amount.setScale(2)}",
                    style = MaterialTheme.typography.headlineMedium,
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Remaining balance: " +
                            "₱${result.remainingBalance.setScale(2)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            is SendMoneyResult.Error -> {

                Text(
                    text = "Unable to Send Money",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = result.message,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Done")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SendMoneyResultContentSuccessPreview() {
    SendMoneyTheme {
        SendMoneyResultContent(
            result = SendMoneyResult.Success(
                amount = BigDecimal("200.00"),
                remainingBalance = BigDecimal("300.00")
            ),
            onDoneClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyResultContentErrorPreview() {
    SendMoneyTheme {
        SendMoneyResultContent(
            result = SendMoneyResult.Error(
                message = "Low Balance"
            ),
            onDoneClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyScreenPreview() {
    SendMoneyTheme {
        SendMoneyScreen(
            uiState = SendMoneyUiState(
                amount = "100"
            ),
            onAmountChanged = {},
            onSubmitClick = {},
            onDismissResult = {},
            onBackClick = {}
        )
    }
}