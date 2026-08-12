package com.jantiojo.sendmoney.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object SendMoney : AppRoute

    @Serializable
    data object Transactions : AppRoute
}
