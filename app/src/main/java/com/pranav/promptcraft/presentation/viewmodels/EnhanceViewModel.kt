package com.pranav.promptcraft.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.pranav.promptcraft.domain.repository.PromptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EnhanceUiState {
    object Idle : EnhanceUiState
    object Loading : EnhanceUiState
    data class Success(
        val response: String,
        val isQuestion: Boolean = false,
        val isComplete: Boolean = false
    ) : EnhanceUiState
    data class Error(val message: String) : EnhanceUiState
}

@HiltViewModel
class EnhanceViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<EnhanceUiState>(EnhanceUiState.Idle)
    val uiState: StateFlow<EnhanceUiState> = _uiState.asStateFlow()
    
    private var currentChat: Chat? = null
    private var originalPrompt: String = ""
    private var selectedTypes: List<String> = emptyList()
    
    fun startEnhancement(prompt: String, types: List<String>) {
        originalPrompt = prompt
        selectedTypes = types
        _uiState.value = EnhanceUiState.Loading
        
        viewModelScope.launch {
            try {
                val chatWithResponse = promptRepository.startEnhancementChat(prompt, types)
                currentChat = chatWithResponse.chat
                
                val response = chatWithResponse.initialResponse
                val isQuestion = !response.startsWith("Enhanced Prompt:")
                val isComplete = response.startsWith("Enhanced Prompt:")
                
                _uiState.value = EnhanceUiState.Success(
                    response = response,
                    isQuestion = isQuestion,
                    isComplete = isComplete
                )
            } catch (e: Exception) {
                _uiState.value = EnhanceUiState.Error(
                    "Failed to start enhancement: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }
    
    fun sendFollowUp(answer: String) {
        val chat = currentChat
        if (chat == null) {
            _uiState.value = EnhanceUiState.Error("No active conversation. Please start a new enhancement.")
            return
        }
        
        _uiState.value = EnhanceUiState.Loading
        
        viewModelScope.launch {
            try {
                val response = promptRepository.sendFollowUpMessage(chat, answer)
                val isComplete = response.startsWith("Enhanced Prompt:")
                
                _uiState.value = EnhanceUiState.Success(
                    response = response,
                    isQuestion = !isComplete,
                    isComplete = isComplete
                )
            } catch (e: Exception) {
                _uiState.value = EnhanceUiState.Error(
                    "Failed to send follow-up: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }
    
    fun resetEnhancement() {
        currentChat = null
        originalPrompt = ""
        selectedTypes = emptyList()
        _uiState.value = EnhanceUiState.Idle
    }
    
    fun getCurrentStep(): String {
        return when (val state = _uiState.value) {
            is EnhanceUiState.Success -> {
                if (state.isComplete) {
                    "Complete"
                } else if (state.isQuestion) {
                    "Waiting for your answer"
                } else {
                    "Processing..."
                }
            }
            is EnhanceUiState.Loading -> "Processing..."
            is EnhanceUiState.Error -> "Error occurred"
            EnhanceUiState.Idle -> "Ready to start"
        }
    }
}
