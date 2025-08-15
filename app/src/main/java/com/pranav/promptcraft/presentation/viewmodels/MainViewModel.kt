package com.pranav.promptcraft.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranav.promptcraft.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing global app state, primarily theme preferences
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    /**
     * StateFlow that emits the current theme preference
     */
    val isDarkTheme: StateFlow<Boolean> = settingsDataStore.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true // Default to dark theme
        )

    /**
     * Toggle the theme and save the preference
     */
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveThemePreference(isDark)
        }
    }
}
