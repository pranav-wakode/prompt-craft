package com.pranav.promptcraft.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.pranav.promptcraft.BuildConfig
/**
 * Hilt module for providing network and API dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        // Replace "your-api-key-here" with your actual Gemini API key
        // In production, store this securely (e.g., using BuildConfig or secure storage)
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY // TODO: Replace with actual API key
        )
    }
}
