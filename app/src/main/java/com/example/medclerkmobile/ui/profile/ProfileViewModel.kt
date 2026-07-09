package com.example.medclerkmobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileData(
    val user: User,
    val averageScorePct: Float?,
    val encounterCount: Int,
    val feedbackCount: Int,
    val rotations: List<Rotation>,
    val competencyCount: Int,
    val overallCompetencyPct: Float?,
)

class ProfileViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<UiState<ProfileData>>(UiState.Loading)
    val state: StateFlow<UiState<ProfileData>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val user = container.authRepository.currentUser()
            val rotations = container.rotationRepository.myRotations()
            val logbookEntries = container.logbookRepository.myEntries()
            val assessments = container.assessmentRepository.myAssessments()
            val feedback = container.feedbackRepository.myFeedback()
            val systems = container.libraryRepository.systems()

            val failure = listOf(user, rotations, logbookEntries, assessments, feedback, systems)
                .firstOrNull { it.isFailure }
            if (failure != null) {
                _state.value = UiState.Error(failure.exceptionOrNull()?.message ?: "Couldn't load your profile.")
                return@launch
            }

            val assessmentList = assessments.getOrThrow()
            val scores = assessmentList.mapNotNull { assessment ->
                val score = assessment.score.toFloatOrNull()
                val maxScore = assessment.maxScore.toFloatOrNull()
                if (score != null && maxScore != null && maxScore > 0f) (score / maxScore) * 100f else null
            }

            val ratedSystems = systems.getOrThrow().filter { it.masteryPct != null }

            _state.value = UiState.Success(
                ProfileData(
                    user = user.getOrThrow(),
                    averageScorePct = scores.takeIf { it.isNotEmpty() }?.let { it.sum() / it.size },
                    encounterCount = logbookEntries.getOrThrow().size,
                    feedbackCount = feedback.getOrThrow().size,
                    rotations = rotations.getOrThrow(),
                    competencyCount = ratedSystems.size,
                    overallCompetencyPct = ratedSystems.takeIf { it.isNotEmpty() }
                        ?.let { list -> list.sumOf { it.masteryPct!!.toDouble() }.toFloat() / list.size },
                ),
            )
        }
    }
}
