package com.example.medclerkmobile.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.data.repository.StudentLookupRepository
import com.example.medclerkmobile.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentProfileViewModel(private val repository: StudentLookupRepository, private val studentId: Int) : ViewModel() {
    private val _state = MutableStateFlow<UiState<User>>(UiState.Loading)
    val state: StateFlow<UiState<User>> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repository.show(studentId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Couldn't load this student.") }
        }
    }
}
