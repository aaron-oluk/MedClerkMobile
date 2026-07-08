package com.example.medclerkmobile.ui.logbook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.NewLogbookEntry
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.data.repository.LogbookRepository
import com.example.medclerkmobile.data.repository.RotationRepository
import com.example.medclerkmobile.ui.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NewLogbookEntryOptions(
    val rotations: List<Rotation>,
    val clinicalSigns: List<NamedRef>,
    val skills: List<NamedRef>,
)

class NewLogbookEntryViewModel(
    private val logbookRepository: LogbookRepository,
    private val rotationRepository: RotationRepository,
) : ViewModel() {
    private val _options = MutableStateFlow<UiState<NewLogbookEntryOptions>>(UiState.Loading)
    val options: StateFlow<UiState<NewLogbookEntryOptions>> = _options

    var selectedRotation by mutableStateOf<Rotation?>(null)
        private set
    var selectedClinicalSign by mutableStateOf<NamedRef?>(null)
        private set
    var selectedSkill by mutableStateOf<NamedRef?>(null)
        private set
    var encounterDate by mutableStateOf(today())
        private set
    var notes by mutableStateOf("")
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
            val rotations = rotationRepository.myRotations()
            val signs = logbookRepository.clinicalSigns()
            val skills = logbookRepository.skills()

            val failure = listOf(rotations, signs, skills).firstOrNull { it.isFailure }
            if (failure != null) {
                _options.value = UiState.Error(failure.exceptionOrNull()?.message ?: "Couldn't load form options.")
                return@launch
            }

            val rotationList = rotations.getOrThrow()
            _options.value = UiState.Success(
                NewLogbookEntryOptions(rotationList, signs.getOrThrow(), skills.getOrThrow()),
            )
            selectedRotation = rotationList.firstOrNull()
        }
    }

    fun onRotationSelected(rotation: Rotation) {
        selectedRotation = rotation
    }

    fun onClinicalSignSelected(sign: NamedRef?) {
        selectedClinicalSign = sign
    }

    fun onSkillSelected(skill: NamedRef?) {
        selectedSkill = skill
    }

    fun onEncounterDateChange(value: String) {
        encounterDate = value
    }

    fun onNotesChange(value: String) {
        notes = value
    }

    fun submit(onSuccess: () -> Unit) {
        val rotation = selectedRotation
        if (rotation == null) {
            errorMessage = "Select a rotation first."
            return
        }

        isSubmitting = true
        errorMessage = null

        viewModelScope.launch {
            val result = logbookRepository.createEntry(
                NewLogbookEntry(
                    rotationId = rotation.id,
                    clinicalSignId = selectedClinicalSign?.id,
                    skillId = selectedSkill?.id,
                    encounterDate = encounterDate,
                    notes = notes.ifBlank { null },
                ),
            )
            isSubmitting = false
            result
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Couldn't save the entry." }
        }
    }

    private companion object {
        fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
