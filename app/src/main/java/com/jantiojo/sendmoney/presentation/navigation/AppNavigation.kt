package com.jantiojo.sendmoney.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jantiojo.sendmoney.presentation.login.LoginScreen

@Composable
fun AppNavigation(
    navHostController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navHostController,
        startDestination = AppRoute.Login
    ) {

        composable<AppRoute.Login> {
            LoginScreen()
        }
    }

}
