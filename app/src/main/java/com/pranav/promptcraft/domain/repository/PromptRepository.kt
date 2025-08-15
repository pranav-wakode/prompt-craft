package com.pranav.promptcraft.domain.repository

import com.google.ai.client.generativeai.Chat
import com.pranav.promptcraft.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

data class ChatWithResponse(
    val chat: Chat,
    val initialResponse: String
)

/**
 * Repository interface for prompt operations
 */
interface PromptRepository {
    suspend fun insertPrompt(prompt: Prompt): Long
    suspend fun getAllPrompts(): Flow<List<Prompt>>
    suspend fun getPromptById(id: Long): Prompt?
    suspend fun deletePrompt(prompt: Prompt)
    suspend fun startEnhancementChat(originalPrompt: String, selectedTypes: List<String>): ChatWithResponse
    suspend fun sendFollowUpMessage(chat: Chat, message: String): String
    
    // Temporary compatibility method - will be removed
    @Deprecated("Use EnhanceViewModel with startEnhancementChat instead")
    suspend fun enhancePrompt(originalPrompt: String, selectedTypes: List<String>): String
}
