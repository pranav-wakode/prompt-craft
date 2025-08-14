package com.pranav.promptcraft.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.pranav.promptcraft.domain.model.Prompt

/**
 * Room database for PromptCraft application
 */
@Database(
    entities = [Prompt::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PromptCraftDatabase : RoomDatabase() {
    
    abstract fun promptDao(): PromptDao
    
    companion object {
        const val DATABASE_NAME = "promptcraft_database"
    }
}
