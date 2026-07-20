package com.example.medclerkmobile.ui.assessments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.data.repository.AssessmentRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AssessmentDetailViewModel(
    private val assessmentRepository: AssessmentRepository,
    private val assessmentId: Int,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Assessment>>(UiState.Loading)
    val state: StateFlow<UiState<Assessment>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            assessmentRepository.assessment(assessmentId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Couldn't load this assessment.") }
        }
    }
}
