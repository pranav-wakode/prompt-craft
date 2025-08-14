package com.pranav.promptcraft.data.database

import androidx.room.*
import com.pranav.promptcraft.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for prompt database operations
 */
@Dao
interface PromptDao {
    
    @Query("SELECT * FROM prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<Prompt>>
    
    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: Long): Prompt?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: Prompt): Long
    
    @Delete
    suspend fun deletePrompt(prompt: Prompt)
    
    @Query("DELETE FROM prompts")
    suspend fun deleteAllPrompts()
}
