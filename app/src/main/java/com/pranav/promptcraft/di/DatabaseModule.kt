package com.pranav.promptcraft.di

import android.content.Context
import androidx.room.Room
import com.pranav.promptcraft.data.database.PromptCraftDatabase
import com.pranav.promptcraft.data.database.PromptDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PromptCraftDatabase {
        return Room.databaseBuilder(
            context,
            PromptCraftDatabase::class.java,
            PromptCraftDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePromptDao(database: PromptCraftDatabase): PromptDao {
        return database.promptDao()
    }
}
