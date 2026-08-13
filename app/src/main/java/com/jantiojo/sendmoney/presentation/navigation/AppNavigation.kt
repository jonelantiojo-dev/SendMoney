package com.jantiojo.sendmoney.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jantiojo.sendmoney.presentation.home.HomeScreenRoute
import com.jantiojo.sendmoney.presentation.login.LoginScreenRoute
import com.jantiojo.sendmoney.presentation.sendmoney.SendMoneyScreenRoute
import com.jantiojo.sendmoney.presentation.transactions.TransactionsScreenRoute

@Composable
fun AppNavigation(
    isLoggedIn: Boolean,
    navHostController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navHostController,
        startDestination = if (isLoggedIn) AppRoute.Home else AppRoute.Login
    ) {

        composable<AppRoute.Login> {
            LoginScreenRoute(
                onLoginSuccess = {
                    navHostController.navigate(AppRoute.Home) {
                        popUpTo<AppRoute.Login> {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable<AppRoute.Home> {
            HomeScreenRoute(
                onSendMoneyClick = {
                    navHostController.navigate(AppRoute.SendMoney)
                },
                onViewTransactionsClick = {
                    navHostController.navigate(AppRoute.Transactions)
                },
                onLogoutSuccess = {
                    navHostController.navigate(AppRoute.Login) {
                        popUpTo(0) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable<AppRoute.SendMoney> {
            SendMoneyScreenRoute(
                onBackClick = {
                    navHostController.popBackStack()
                }
            )
        }

        composable<AppRoute.Transactions> {
            TransactionsScreenRoute(
                onBackClick = {
                    navHostController.popBackStack()
                }
            )
        }
    }

}
