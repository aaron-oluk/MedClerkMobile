package com.example.medclerkmobile.ui.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.repository.LibraryRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LibraryData(
    val systems: List<ClinicalSystem>,
    val recentSigns: List<ClinicalSign>,
)

class LibraryViewModel(private val library: LibraryRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<LibraryData>>(UiState.Loading)
    val state: StateFlow<UiState<LibraryData>> = _state

    var query by mutableStateOf("")
        private set

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val systems = library.systems()
            val signs = library.allSigns()

            val failure = listOf(systems, signs).firstOrNull { it.isFailure }
            if (failure != null) {
                _state.value = UiState.Error(failure.exceptionOrNull()?.message ?: "Couldn't load the library.")
                return@launch
            }

            _state.value = UiState.Success(LibraryData(systems.getOrThrow(), signs.getOrThrow()))
        }
    }

    fun onQueryChange(value: String) {
        query = value
    }

    fun matchingSigns(signs: List<ClinicalSign>): List<ClinicalSign> {
        if (query.isBlank()) return emptyList()
        val needle = query.trim()
        return signs.filter { sign ->
            sign.name.contains(needle, ignoreCase = true) ||
                sign.tags.any { it.name.contains(needle, ignoreCase = true) }
        }
    }

    fun matchingSystems(systems: List<ClinicalSystem>): List<ClinicalSystem> {
        if (query.isBlank()) return systems
        return systems.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
}
