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
    private val logbookEntryId: Int,
) : ViewModel() {
    private val _entry = MutableStateFlow<UiState<LogbookEntry>>(UiState.Loading)
    val entry: StateFlow<UiState<LogbookEntry>> = _entry

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
        loadEntry()
    }

    private fun loadEntry() {
        _entry.value = UiState.Loading
        viewModelScope.launch {
            logbookRepository.entry(logbookEntryId)
                .onSuccess { _entry.value = UiState.Success(it) }
                .onFailure { _entry.value = UiState.Error(it.message ?: "Couldn't load this encounter.") }
        }
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
        val scoreValue = score.toDoubleOrNull()
        val maxScoreValue = maxScore.toDoubleOrNull()

        if (scoreValue == null || maxScoreValue == null || maxScoreValue <= 0 || scoreValue > maxScoreValue) {
            errorMessage = "Enter a valid score and max score."
            return
        }

        isSubmitting = true
        errorMessage = null

        viewModelScope.launch {
            val result = assessmentRepository.createAssessment(
                NewAssessment(
                    logbookEntryId = logbookEntryId,
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
