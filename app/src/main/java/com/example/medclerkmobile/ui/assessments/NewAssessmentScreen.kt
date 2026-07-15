package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.ui.DateField
import com.example.medclerkmobile.ui.DropdownField
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAssessmentScreen(container: AppContainer, onSaved: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container, key = "new-assessment") {
        NewAssessmentViewModel(it.assessmentRepository, it.logbookRepository)
    }
    val optionsState by viewModel.options.collectAsState()

    Scaffold(
        topBar = { ScreenHeader(title = "Give an assessment", onBack = onCancel) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = optionsState) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> if (state.data.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No unscored encounters from your students right now.",
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    NewAssessmentForm(viewModel = viewModel, logEntries = state.data, onSaved = onSaved)
                }
            }
        }
    }
}

@Composable
private fun NewAssessmentForm(viewModel: NewAssessmentViewModel, logEntries: List<LogbookEntry>, onSaved: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Encounter")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DropdownField(
                        label = "Student and encounter",
                        options = logEntries,
                        selected = viewModel.selectedLogEntry,
                        optionLabel = { entry ->
                            "${entry.student?.name ?: "Student"} — ${entry.skill?.name ?: "Skill"} (${formatApiDate(entry.encounterDate)})"
                        },
                        onSelected = viewModel::onLogEntrySelected,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    DateField(
                        label = "Assessed on",
                        value = viewModel.assessedAt,
                        onValueChange = viewModel::onAssessedAtChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Score")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = viewModel.score,
                        onValueChange = viewModel::onScoreChange,
                        label = { Text("Score") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = viewModel.maxScore,
                        onValueChange = viewModel::onMaxScoreChange,
                        label = { Text("Out of") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.submit(onSaved) },
            enabled = !viewModel.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (viewModel.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Save assessment", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
