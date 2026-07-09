package com.example.medclerkmobile.ui.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.data.repository.LibraryRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SkillDetailViewModel(private val library: LibraryRepository, private val skillId: Int) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Skill>>(UiState.Loading)
    val state: StateFlow<UiState<Skill>> = _state

    var activeStep by mutableIntStateOf(0)
        private set

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            library.skill(skillId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Couldn't load this skill.") }
        }
    }

    fun goToStep(index: Int) {
        activeStep = index
    }
}
