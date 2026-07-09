package com.example.medclerkmobile.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.data.repository.LibraryRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SystemDetailData(
    val system: ClinicalSystem,
    val signs: List<ClinicalSign>,
    val skills: List<Skill>,
)

class SystemDetailViewModel(private val library: LibraryRepository, private val systemId: Int) : ViewModel() {
    private val _state = MutableStateFlow<UiState<SystemDetailData>>(UiState.Loading)
    val state: StateFlow<UiState<SystemDetailData>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val system = library.system(systemId)
            val signs = library.signsBySystem(systemId)
            val skills = library.skillsBySystem(systemId)

            val failure = listOf(system, signs, skills).firstOrNull { it.isFailure }
            if (failure != null) {
                _state.value = UiState.Error(failure.exceptionOrNull()?.message ?: "Couldn't load this system.")
                return@launch
            }

            _state.value = UiState.Success(SystemDetailData(system.getOrThrow(), signs.getOrThrow(), skills.getOrThrow()))
        }
    }
}
