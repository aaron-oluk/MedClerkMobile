package com.example.medclerkmobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsData(val emailNotificationsEnabled: Boolean)

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<UiState<SettingsData>>(UiState.Loading)
    val state: StateFlow<UiState<SettingsData>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            container.authRepository.currentUser()
                .onSuccess { _state.value = UiState.Success(SettingsData(it.emailNotificationsEnabled)) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Couldn't load settings.") }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        val current = (_state.value as? UiState.Success)?.data ?: return
        _state.value = UiState.Success(current.copy(emailNotificationsEnabled = enabled))
        viewModelScope.launch {
            container.settingsRepository.updateNotifications(enabled)
        }
    }
}
