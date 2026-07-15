package com.example.medclerkmobile.ui.assessments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.data.model.NewAssessment
import com.example.medclerkmobile.data.repository.AssessmentRepository
import com.example.medclerkmobile.data.repository.LogbookRepository
import com.example.medclerkmobile.ui.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewAssessmentViewModel(
    private val assessmentRepository: AssessmentRepository,
    private val logbookRepository: LogbookRepository,
) : ViewModel() {
    private val _options = MutableStateFlow<UiState<List<LogbookEntry>>>(UiState.Loading)
    val options: StateFlow<UiState<List<LogbookEntry>>> = _options

    var selectedLogEntry by mutableStateOf<LogbookEntry?>(null)
        private set
    var score by mutableStateOf("")
        private set
    var maxScore by mutableStateOf("100")
        private set
    var assessedAt by mutableStateOf(today())
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
            logbookRepository.pendingAssessments()
                .onSuccess {
                    _options.value = UiState.Success(it)
                    selectedLogEntry = it.firstOrNull()
                }
                .onFailure { _options.value = UiState.Error(it.message ?: "Couldn't load your students' logged encounters.") }
        }
    }

    fun onLogEntrySelected(entry: LogbookEntry) {
        selectedLogEntry = entry
    }

    fun onScoreChange(value: String) {
        score = value
    }

    fun onMaxScoreChange(value: String) {
        maxScore = value
    }

    fun onAssessedAtChange(value: String) {
        assessedAt = value
    }

    fun submit(onSuccess: () -> Unit) {
        val entry = selectedLogEntry
        val scoreValue = score.toDoubleOrNull()
        val maxScoreValue = maxScore.toDoubleOrNull()

        if (entry == null) {
            errorMessage = "Select a logged encounter first."
            return
        }
        if (scoreValue == null || maxScoreValue == null || maxScoreValue <= 0 || scoreValue > maxScoreValue) {
            errorMessage = "Enter a valid score and max score."
            return
        }

        isSubmitting = true
        errorMessage = null

        viewModelScope.launch {
            val result = assessmentRepository.createAssessment(
                NewAssessment(
                    logbookEntryId = entry.id,
                    score = scoreValue,
                    maxScore = maxScoreValue,
                    assessedAt = assessedAt,
                ),
            )
            isSubmitting = false
            result
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Couldn't save the assessment." }
        }
    }

    private companion object {
        fun today(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
    }
}
