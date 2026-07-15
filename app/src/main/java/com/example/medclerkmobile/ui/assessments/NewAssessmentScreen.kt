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
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAssessmentScreen(container: AppContainer, logbookEntryId: Int, onSaved: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container, key = "new-assessment") {
        NewAssessmentViewModel(it.assessmentRepository, it.logbookRepository, logbookEntryId)
    }
    val entryState by viewModel.entry.collectAsState()

    Scaffold(
        topBar = { ScreenHeader(title = "Give an assessment", onBack = onCancel) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = entryState) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> NewAssessmentForm(viewModel = viewModel, entry = state.data, onSaved = onSaved)
            }
        }
    }
}

private fun LogbookEntry.findingText(key: String): String? {
    val element = findings?.get(key) ?: return null
    if (element is JsonNull) return null
    return element.jsonPrimitive.content.takeIf { it.isNotBlank() }
}

@Composable
private fun NewAssessmentForm(viewModel: NewAssessmentViewModel, entry: LogbookEntry, onSaved: () -> Unit) {
    val chiefComplaint = entry.findingText("chief_complaint")
    val examinationFindings = entry.findingText("examination_findings")
    val impression = entry.findingText("impression")
    val plan = entry.findingText("plan")
    val hasNothingToShow = chiefComplaint == null && examinationFindings == null && impression == null && plan == null && entry.notes == null

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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${entry.student?.name ?: "Student"} — ${entry.skill?.name ?: "Skill"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${formatApiDate(entry.encounterDate)} · ${entry.rotation?.name ?: "Rotation"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    FindingBlock("Chief complaint", chiefComplaint)
                    FindingBlock("Examination findings", examinationFindings)
                    FindingBlock("Impression", impression)
                    FindingBlock("Plan", plan)
                    FindingBlock("Notes", entry.notes)

                    if (hasNothingToShow) {
                        Text(
                            text = "No structured findings recorded for this encounter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Score")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

                    DateField(
                        label = "Assessed on",
                        value = viewModel.assessedAt,
                        onValueChange = viewModel::onAssessedAtChange,
                        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun FindingBlock(label: String, value: String?) {
    if (value == null) return

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
