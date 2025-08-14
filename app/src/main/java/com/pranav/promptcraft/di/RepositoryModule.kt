package com.pranav.promptcraft.di

import com.pranav.promptcraft.data.repository.AuthRepositoryImpl
import com.pranav.promptcraft.data.repository.PromptRepositoryImpl
import com.pranav.promptcraft.domain.repository.AuthRepository
import com.pranav.promptcraft.domain.repository.PromptRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations to interfaces
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPromptRepository(
        promptRepositoryImpl: PromptRepositoryImpl
    ): PromptRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
