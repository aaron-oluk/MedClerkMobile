package com.example.medclerkmobile.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.repository.LibraryRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignDetailViewModel(private val library: LibraryRepository, private val signId: Int) : ViewModel() {
    private val _state = MutableStateFlow<UiState<ClinicalSign>>(UiState.Loading)
    val state: StateFlow<UiState<ClinicalSign>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            library.sign(signId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Couldn't load this sign.") }
        }
    }
}
