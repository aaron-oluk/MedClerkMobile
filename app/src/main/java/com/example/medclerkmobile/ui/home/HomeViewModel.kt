package com.example.medclerkmobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeData(
    val activeRotation: Rotation?,
    val assessmentCount: Int,
    val feedbackCount: Int,
    val strongestSystem: ClinicalSystem?,
    val weakestSystem: ClinicalSystem?,
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val state: StateFlow<UiState<HomeData>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val rotations = container.rotationRepository.myRotations()
            val assessments = container.assessmentRepository.myAssessments()
            val feedback = container.feedbackRepository.myFeedback()
            val systems = container.libraryRepository.systems()

            val failure = listOf(rotations, assessments, feedback, systems).firstOrNull { it.isFailure }
            if (failure != null) {
                _state.value = UiState.Error(failure.exceptionOrNull()?.message ?: "Couldn't load your dashboard.")
                return@launch
            }

            val rated = systems.getOrThrow().filter { it.masteryPct != null }
            val strongest = rated.maxByOrNull { it.masteryPct!! }
            val weakest = rated.minByOrNull { it.masteryPct!! }

            _state.value = UiState.Success(
                HomeData(
                    activeRotation = rotations.getOrThrow().firstOrNull { it.status == "active" }
                        ?: rotations.getOrThrow().firstOrNull(),
                    assessmentCount = assessments.getOrThrow().size,
                    feedbackCount = feedback.getOrThrow().size,
                    strongestSystem = strongest,
                    weakestSystem = weakest?.takeIf { it.id != strongest?.id },
                ),
            )
        }
    }
}
