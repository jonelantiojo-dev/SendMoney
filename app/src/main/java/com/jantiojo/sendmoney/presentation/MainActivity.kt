package com.jantiojo.sendmoney.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jantiojo.sendmoney.presentation.navigation.AppNavigation
import com.jantiojo.sendmoney.presentation.ui.theme.SendMoneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SendMoneyTheme {
                AppNavigation()
            }
        }
    }
}
