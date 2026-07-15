package com.example.medclerkmobile.ui.students

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentSearchViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<User>>>(UiState.Success(emptyList()))
    val state: StateFlow<UiState<List<User>>> = _state

    var query by mutableStateOf("")
        private set

    fun onQueryChange(value: String) {
        query = value
    }

    fun search() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            container.studentLookupRepository.search(query)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Search failed.") }
        }
    }
}
