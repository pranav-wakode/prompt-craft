package com.pranav.promptcraft.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranav.promptcraft.domain.model.Prompt
import com.pranav.promptcraft.domain.model.PromptType
import com.pranav.promptcraft.domain.repository.PromptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main prompt enhancement screen
 */
@HiltViewModel
class PromptEnhancerViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptEnhancerUiState())
    val uiState: StateFlow<PromptEnhancerUiState> = _uiState.asStateFlow()

    fun updateInputPrompt(prompt: String) {
        _uiState.value = _uiState.value.copy(inputPrompt = prompt)
    }

    fun selectPromptType(type: PromptType) {
        val currentState = _uiState.value
        val newSelectedTypes = if (type == PromptType.AUTO) {
            // If AUTO is selected, deselect all others
            setOf(PromptType.AUTO)
        } else {
            // If other type is selected, deselect AUTO
            val updatedTypes = currentState.selectedPromptTypes.toMutableSet()
            updatedTypes.remove(PromptType.AUTO)
            
            if (updatedTypes.contains(type)) {
                updatedTypes.remove(type)
            } else {
                updatedTypes.add(type)
            }
            
            // If no types selected, default back to AUTO
            if (updatedTypes.isEmpty()) {
                setOf(PromptType.AUTO)
            } else {
                updatedTypes
            }
        }
        
        _uiState.value = currentState.copy(selectedPromptTypes = newSelectedTypes)
    }

    fun enhancePrompt() {
        val currentState = _uiState.value
        if (currentState.inputPrompt.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            try {
                val selectedTypeNames = currentState.selectedPromptTypes.map { it.displayName }
                val enhancedText = promptRepository.enhancePrompt(
                    currentState.inputPrompt,
                    selectedTypeNames
                )

                // Check if the response is a follow-up question
                if (isFollowUpQuestion(enhancedText)) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        followUpQuestion = enhancedText,
                        showFollowUpDialog = true
                    )
                } else {
                    // Save to history and navigate to result
                    val prompt = Prompt(
                        originalPrompt = currentState.inputPrompt,
                        enhancedPrompt = enhancedText,
                        promptTypes = selectedTypeNames
                    )
                    promptRepository.insertPrompt(prompt)

                    _uiState.value = currentState.copy(
                        isLoading = false,
                        enhancedPrompt = enhancedText,
                        showResult = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "An error occurred"
                )
            }
        }
    }

    fun answerFollowUpQuestion(answer: String) {
        val currentState = _uiState.value
        val combinedPrompt = "${currentState.inputPrompt}\n\nAdditional context: $answer"
        
        _uiState.value = currentState.copy(
            inputPrompt = combinedPrompt,
            showFollowUpDialog = false,
            followUpQuestion = null
        )
        
        // Enhance with the updated prompt
        enhancePrompt()
    }

    fun dismissFollowUpDialog() {
        _uiState.value = _uiState.value.copy(
            showFollowUpDialog = false,
            followUpQuestion = null,
            isLoading = false
        )
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(
            showResult = false,
            enhancedPrompt = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isFollowUpQuestion(response: String): Boolean {
        // Simple heuristic to detect if response is a question
        val questionIndicators = listOf("?", "what", "how", "when", "where", "why", "which", "can you", "could you")
        return questionIndicators.any { indicator ->
            response.lowercase().contains(indicator)
        } && !response.contains("Enhanced Prompt:")
    }
}

data class PromptEnhancerUiState(
    val inputPrompt: String = "",
    val selectedPromptTypes: Set<PromptType> = setOf(PromptType.AUTO),
    val isLoading: Boolean = false,
    val enhancedPrompt: String? = null,
    val showResult: Boolean = false,
    val followUpQuestion: String? = null,
    val showFollowUpDialog: Boolean = false,
    val error: String? = null
)
