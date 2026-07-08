package com.example.medclerkmobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ListViewModel<T>(private val load: suspend () -> Result<List<T>>) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<T>>>(UiState.Loading)
    val state: StateFlow<UiState<List<T>>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            load()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Something went wrong.") }
        }
    }
}
