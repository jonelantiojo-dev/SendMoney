package com.jantiojo.sendmoney.di

import com.jantiojo.sendmoney.data.repository.SessionRepositoryImpl
import com.jantiojo.sendmoney.data.repository.WalletRepositoryImpl
import com.jantiojo.sendmoney.domain.repository.SessionRepository
import com.jantiojo.sendmoney.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        implementation: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        implementation: WalletRepositoryImpl
    ): WalletRepository
}
