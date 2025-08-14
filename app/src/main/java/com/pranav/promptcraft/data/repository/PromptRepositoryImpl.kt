package com.pranav.promptcraft.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.pranav.promptcraft.data.database.PromptDao
import com.pranav.promptcraft.domain.model.Prompt
import com.pranav.promptcraft.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PromptRepository that handles both local database and AI operations
 */
@Singleton
class PromptRepositoryImpl @Inject constructor(
    private val promptDao: PromptDao,
    private val generativeModel: GenerativeModel
) : PromptRepository {

    override suspend fun insertPrompt(prompt: Prompt): Long {
        return promptDao.insertPrompt(prompt)
    }

    override suspend fun getAllPrompts(): Flow<List<Prompt>> {
        return promptDao.getAllPrompts()
    }

    override suspend fun getPromptById(id: Long): Prompt? {
        return promptDao.getPromptById(id)
    }

    override suspend fun deletePrompt(prompt: Prompt) {
        promptDao.deletePrompt(prompt)
    }

    override suspend fun enhancePrompt(originalPrompt: String, selectedTypes: List<String>): String {
        val typesText = if (selectedTypes.contains("Auto")) {
            "Auto"
        } else {
            selectedTypes.joinToString(", ")
        }

        val metaPrompt = buildMetaPrompt(originalPrompt, typesText)
        
        return try {
            val response = generativeModel.generateContent(metaPrompt)
            response.text ?: "Error: Unable to generate enhanced prompt"
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    private fun buildMetaPrompt(userPrompt: String, selectedTypes: String): String {
        return """
            You are an expert prompt engineer. Your task is to enhance the following user prompt to be highly detailed, specific, and effective for a generative AI.

            **Instructions:**
            1. Analyze the user's input. If the user has selected specific prompt engineering techniques ($selectedTypes), you must apply them. If they have selected 'Auto', you must first determine the best technique(s) to use.
            2. **Crucially, if the user's initial prompt is too vague to create a high-quality enhancement (e.g., it lacks context, a clear goal, or specifics), you MUST NOT generate a weak enhancement. Instead, your entire response must be ONLY a single, clarifying follow-up question to get the necessary information from the user.**
            3. If the prompt is clear enough to proceed, generate the fully enhanced, detailed prompt directly.

            Here is the user's input: '$userPrompt'
        """.trimIndent()
    }
}
