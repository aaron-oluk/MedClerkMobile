package com.example.medclerkmobile.ui.feedback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.data.model.NewFeedback
import com.example.medclerkmobile.data.repository.AssessmentRepository
import com.example.medclerkmobile.data.repository.FeedbackRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewFeedbackViewModel(
    private val feedbackRepository: FeedbackRepository,
    private val assessmentRepository: AssessmentRepository,
) : ViewModel() {
    private val _options = MutableStateFlow<UiState<List<Assessment>>>(UiState.Loading)
    val options: StateFlow<UiState<List<Assessment>>> = _options

    var selectedAssessment by mutableStateOf<Assessment?>(null)
        private set
    var strengths by mutableStateOf("")
        private set
    var areasToImprove by mutableStateOf("")
        private set
    var followUpDate by mutableStateOf("")
        private set
    var isSubmitting by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadOptions()
    }

    private fun loadOptions() {
        _options.value = UiState.Loading
        viewModelScope.launch {
            assessmentRepository.myAssessments()
                .onSuccess {
                    _options.value = UiState.Success(it)
                    selectedAssessment = it.firstOrNull()
                }
                .onFailure { _options.value = UiState.Error(it.message ?: "Couldn't load your assessments.") }
        }
    }

    fun onAssessmentSelected(assessment: Assessment) {
        selectedAssessment = assessment
    }

    fun onStrengthsChange(value: String) {
        strengths = value
    }

    fun onAreasToImproveChange(value: String) {
        areasToImprove = value
    }

    fun onFollowUpDateChange(value: String) {
        followUpDate = value
    }

    fun submit(onSuccess: () -> Unit) {
        val assessment = selectedAssessment
        if (assessment == null) {
            errorMessage = "Select an assessment first."
            return
        }

        isSubmitting = true
        errorMessage = null

        viewModelScope.launch {
            val result = feedbackRepository.createFeedback(
                NewFeedback(
                    assessmentId = assessment.id,
                    strengths = strengths.ifBlank { null },
                    areasToImprove = areasToImprove.ifBlank { null },
                    followUpDate = followUpDate.ifBlank { null },
                ),
            )
            isSubmitting = false
            result
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Couldn't save the feedback." }
        }
    }
}
